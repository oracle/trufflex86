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
package org.graalvm.vm.x86.test.runner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

        Source source = Source.newBuilder(Vmx86.NAME, path, "<path>").build();
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

        Source source = Source.newBuilder(Vmx86.NAME, path, "<path>").build();
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

        Source source = Source.newBuilder(Vmx86.NAME, path, "<path>").build();
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
