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

import org.graalvm.vm.x86.isa.instruction.Shl.Shlb;
import org.graalvm.vm.x86.isa.instruction.Shl.Shlq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class ShlTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, (byte) 0xc1, (byte) 0xe2, 0x20};
    private static final String ASSEMBLY1 = "shl\trdx,0x20";

    private static final byte[] MACHINECODE2 = {(byte) 0xc0, (byte) 0xe2, 0x02};
    private static final String ASSEMBLY2 = "shl\tdl,0x2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shlq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Shlb.class);
    }

    @Test
    public void shl() throws Exception {
        String stdout = "0000:00:0000:0000\n" +
                        "0000:05:0000:0044\n" +
                        "1000:00:1000:0000\n" +
                        "1000:05:0000:0044\n" +
                        "ffff:05:ffe0:0081\n" +
                        "8000:08:0000:0044\n" +
                        "8001:0f:8000:0884\n" +
                        "8001:10:0000:0845\n" +
                        "ffff:0f:8000:0085\n" +
                        "ffff:10:0000:0845\n" +
                        "8000:01:0000:0845\n" +
                        "c000:01:8000:0085\n" +
                        "c000:02:0000:0845\n" +
                        "c000:02:0000:0845\n" +
                        "c000:04:0000:0044\n" +
                        "c000:00:c000:0000\n" +
                        "c0de:00:c0de:0000\n" +
                        "efde:08:de00:0085\n" +
                        "efde:04:fde0:0880\n" +
                        "0000:01:0000:0044\n" +
                        "1000:01:2000:0004\n" +
                        "0000:02:0000:0044\n" +
                        "1000:02:4000:0004\n" +
                        "c0de:10:0000:0044\n" +
                        "c0de:18:0000:0044\n" +
                        "c0de:20:c0de:0000\n" +
                        "c0de:30:0000:0044\n" +
                        "c0de:38:0000:0044\n" +
                        "c0de:40:c0de:0000\n" +
                        "dffffdea:1:bffffbd4:0085\n" +
                        "0ffffdea:1:1ffffbd4:0004\n" +
                        "0ffffde0:1:1ffffbc0:0004\n" +
                        "0000000000000163:20:0000016300000000:0004\n";
        TestRunner.run("shl.elf", new String[0], "", stdout, "", 0);
    }
}
