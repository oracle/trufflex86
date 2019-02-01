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
package org.graalvm.vm.x86.test;

import org.graalvm.vm.x86.isa.instruction.Pand;
import org.graalvm.vm.x86.isa.instruction.Pcmpgt.Pcmpgt128b;
import org.graalvm.vm.x86.isa.instruction.Pmaxub;
import org.junit.Test;

public class SseTest extends InstructionTest {
    private static final byte[] MACHINECODE_PMAXUB = {0x66, 0x0f, (byte) 0xde, (byte) 0xd8};
    private static final String ASSEMBLY_PMAXUB = "pmaxub\txmm3,xmm0";

    private static final byte[] MACHINECODE_PCMPGTB = {0x66, 0x44, 0x0f, 0x64, (byte) 0xc5};
    private static final String ASSEMBLY_PCMPGTB = "pcmpgtb\txmm8,xmm5";

    private static final byte[] MACHINECODE_PAND = {0x66, 0x45, 0x0f, (byte) 0xdb, (byte) 0xc1};
    private static final String ASSEMBLY_PAND = "pand\txmm8,xmm9";

    @Test
    public void testPmaxub() {
        check(MACHINECODE_PMAXUB, ASSEMBLY_PMAXUB, Pmaxub.class);
    }

    @Test
    public void testPcmpgtb() {
        check(MACHINECODE_PCMPGTB, ASSEMBLY_PCMPGTB, Pcmpgt128b.class);
    }

    @Test
    public void testPand() {
        check(MACHINECODE_PAND, ASSEMBLY_PAND, Pand.class);
    }
}
