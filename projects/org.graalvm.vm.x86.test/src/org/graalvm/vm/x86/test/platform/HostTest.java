/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.test.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.Vmx86;
import org.graalvm.vm.x86.test.TestOptions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

        Source source = Source.newBuilder(Vmx86.NAME, filename, "<path>").build();
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
