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

import org.graalvm.vm.x86.isa.instruction.Dec.Decb;
import org.graalvm.vm.x86.isa.instruction.Dec.Decl;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class DecTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, (byte) 0xfe, (byte) 0xcd};
    private static final String ASSEMBLY1 = "dec\tr13b";

    private static final byte[] MACHINECODE2 = {(byte) 0xff, (byte) 0xc9};
    private static final String ASSEMBLY2 = "dec\tecx";

    private static final byte[] MACHINECODE3 = {(byte) 0xff, (byte) 0x8b, (byte) 0xd8, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY3 = "dec\t[rbx+0xd8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Decb.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Decl.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Decl.class);
    }
}
