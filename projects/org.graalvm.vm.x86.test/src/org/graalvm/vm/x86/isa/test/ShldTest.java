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
package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Shld;
import org.graalvm.vm.x86.test.InstructionTest;
import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class ShldTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x4c, 0x0f, (byte) 0xa5, (byte) 0xc8};
    private static final String ASSEMBLY1 = "shld\trax,r9,cl";

    private static final byte[] MACHINECODE2 = {0x0f, (byte) 0xa4, (byte) 0xd8, 0x01};
    private static final String ASSEMBLY2 = "shld\teax,ebx,0x1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shld.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Shld.class);
    }

    @Test
    public void shld() throws Exception {
        String stdout = "0000:0000:00:0000:0000\n" +
                        "0000:0000:05:0000:0044\n" +
                        "0000:1000:00:0000:0000\n" +
                        "0000:1000:05:0002:0000\n" +
                        "0000:ffff:05:001f:0000\n" +
                        "0000:8000:08:0080:0000\n" +
                        "0000:8001:0f:4000:0004\n" +
                        "0000:8001:10:8001:0880\n" +
                        "0000:ffff:0f:7fff:0004\n" +
                        "0000:ffff:10:ffff:0884\n" +
                        "0001:8000:01:0003:0004\n" +
                        "0001:c000:01:0003:0004\n" +
                        "0001:c000:02:0007:0000\n" +
                        "1001:c000:02:4007:0000\n" +
                        "1001:c000:04:001c:0001\n" +
                        "1001:c000:00:1001:0000\n" +
                        "beef:c0de:00:beef:0000\n" +
                        "babe:efde:08:beef:0080\n" +
                        "babe:efde:04:abee:0085\n" +
                        "4000:0000:01:8000:0884\n" +
                        "8000:1000:01:0000:0845\n" +
                        "4000:0000:02:0000:0045\n" +
                        "8000:1000:02:0000:0844\n" +
                        "beef:c0de:10:c0de:0085\n" +
                        "beef:c0de:18:ffff:0085\n" +
                        "beef:c0de:20:beef:0000\n" +
                        "beef:c0de:30:c0de:0085\n" +
                        "beef:c0de:38:ffff:0085\n" +
                        "beef:c0de:40:beef:0000\n";
        TestRunner.run("shld.elf", new String[0], "", stdout, "", 0);
    }
}
