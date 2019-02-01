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

import org.junit.Test;

public class HelloWorldInterpreterTest {
    @Test
    public void test1() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("hello.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test2() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("helloworld.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test3() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 1;

        TestRunner.run("hello-strlen.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test4() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("strlen.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test5() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("hello2.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test6() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "fs(selector)=0\n" +
                        "fs=0\n" +
                        "fs(selector)=402048\n" +
                        "fs=402048\n" +
                        "tls=4847464544434241\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("tls.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test7() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "";
        String stderr = "";

        int code = 89;

        TestRunner.run("fib.asm.elf", args, stdin, stdout, stderr, code);
    }
}
