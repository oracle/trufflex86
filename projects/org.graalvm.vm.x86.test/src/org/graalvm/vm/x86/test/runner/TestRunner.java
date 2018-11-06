package org.graalvm.vm.x86.test.runner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.vm.x86.Vmx86;
import org.graalvm.vm.x86.test.TestOptions;

public class TestRunner {
    public static String getPath(String name) {
        return TestOptions.PATH + "/" + name;
    }

    public static String[] getArgs(String name, String[] args) {
        String[] result = new String[args.length + 1];
        result[0] = getPath(name);
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }

    public static void run(String filename, String[] args, byte[] stdin, byte[] stdout, byte[] stderr, int code) throws Exception {
        TestOptions.init();
        String path = getPath(filename);

        Source source = Source.newBuilder(Vmx86.NAME, new File(path)).build();
        ByteArrayInputStream in = new ByteArrayInputStream(stdin);
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

        assertEquals(code, (byte) status); // return code is only 8bit in Linux
        assertArrayEquals(stdout, out.toByteArray());
        assertArrayEquals(stderr, err.toByteArray());
    }

    public static void run(String filename, String[] args, String stdin, String stdout, String stderr, int code) throws Exception {
        TestOptions.init();
        String path = getPath(filename);

        Source source = Source.newBuilder(Vmx86.NAME, new File(path)).build();
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

        assertEquals(code, (byte) status); // return code is only 8bit in Linux
        assertEquals(stdout, new String(out.toByteArray(), StandardCharsets.UTF_8));
        assertEquals(stderr, new String(err.toByteArray(), StandardCharsets.UTF_8));
    }

    public static void runBinary(String filename, String[] args, String stdin, String stdout, String stderr, int code) throws Exception {
        TestOptions.init();
        String path = getPath(filename);

        Source source = Source.newBuilder(Vmx86.NAME, new File(path)).build();
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

        Encoder b64 = Base64.getEncoder();
        assertEquals(code, (byte) status); // return code is only 8bit in Linux
        assertEquals(stdout, b64.encodeToString(out.toByteArray()));
        assertEquals(stderr, new String(err.toByteArray(), StandardCharsets.UTF_8));
    }
}
