package org.graalvm.vm.x86.test.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.vm.x86.Vmx86;
import org.graalvm.vm.x86.test.TestOptions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.everyware.util.log.Trace;

public class HostTest {
    private static final Logger log = Trace.create(HostTest.class);
    public static final boolean isX86 = isx86();

    private static class Result {
        public final String stdout;
        public final String stderr;
        public final int code;

        public Result(String stdout, String stderr, int code) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.code = code;
        }
    }

    public static final boolean isx86() {
        String arch = System.getProperty("os.arch");
        return arch.equalsIgnoreCase("amd64");
    }

    private static String[] getArgs(String name, String[] args) {
        String[] result = new String[args.length + 1];
        result[0] = name;
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }

    private static Result runHost(String filename, String[] args, String stdin) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(getArgs(filename, args));
        Process process = pb.start();
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        ByteArrayOutputStream errbuf = new ByteArrayOutputStream();
        try (InputStream out = process.getInputStream(); InputStream err = process.getErrorStream(); PrintStream in = new PrintStream(process.getOutputStream())) {
            in.print(stdin);
            byte[] bytes = new byte[256];
            int n;
            while ((n = out.read(bytes)) != -1) {
                outbuf.write(bytes, 0, n);
            }
            while ((n = err.read(bytes)) != -1) {
                errbuf.write(bytes, 0, n);
            }
        }
        return new Result(new String(outbuf.toByteArray(), StandardCharsets.UTF_8), new String(errbuf.toByteArray(), StandardCharsets.UTF_8), process.waitFor());
    }

    private static void run(String filename, String[] args, String stdin, Result res) throws Exception {
        TestOptions.init();

        Source source = Source.newBuilder(Vmx86.NAME, new File(filename)).build();
        ByteArrayInputStream in = new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        Value result;
        Context ctx = Context.newBuilder(Vmx86.NAME).arguments(Vmx86.NAME, getArgs(filename, args)).in(in).out(out).err(err).build();

        try {
            result = ctx.eval(source);
        } finally {
            ctx.close();
        }

        assertNotNull(result);

        int status = result.asInt();

        assertEquals(res.code, status);
        assertEquals(res.stdout, new String(out.toByteArray(), StandardCharsets.UTF_8));
        assertEquals(res.stderr, new String(err.toByteArray(), StandardCharsets.UTF_8));
    }

    @BeforeClass
    public static void setupClass() {
        if (!isX86) {
            log.log(Level.WARNING, "Host is not x86, skipping tests which only work on x86 hosts");
        }
    }

    @Before
    public void setup() {
        assumeTrue(isX86);
    }

    public static void run(String filename, String[] args, String stdin) throws Exception {
        if (!isX86) {
            return;
        }
        Result host = runHost(filename, args, stdin);
        run(filename, args, stdin, host);
    }

    @Test
    public void echo() throws Exception {
        run("/bin/echo", new String[]{"hello", "world"}, "");
    }

    @Test
    public void echoHelp() throws Exception {
        run("/bin/echo", new String[]{"--help"}, "");
    }
}
