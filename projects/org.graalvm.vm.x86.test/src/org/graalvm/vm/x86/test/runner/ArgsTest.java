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

public class ArgsTest {
    private static final String FILENAME = "args.nostdlib.elf";

    private static String get(String[] args) {
        StringBuilder buf = new StringBuilder("Arguments: ");
        buf.append(args.length + 1);
        buf.append("\nargs[0] = '");
        buf.append(TestRunner.getPath(FILENAME));
        buf.append("'\n");
        for (int i = 0; i < args.length; i++) {
            buf.append("args[").append(i + 1).append("] = '").append(args[i]).append("'\n");
        }
        return buf.toString();
    }

    @Test
    public void test1() throws Exception {
        String[] args = new String[0];
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test2() throws Exception {
        String[] args = {"Hello"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test3() throws Exception {
        String[] args = {"Hello", "World"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test4() throws Exception {
        String[] args = {"one argument"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test5() throws Exception {
        String[] args = {"multiple long", "arguments with spaces"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test6() throws Exception {
        String[] args = {"special\tchars", "line\nbreaks"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }
}
