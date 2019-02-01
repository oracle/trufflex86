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
package org.graalvm.vm.x86.isa;

import com.oracle.truffle.api.CompilerAsserts;

public class CpuidBits {
    // FN=1: EDX
    public static final int TSC = 1 << 4;
    public static final int CMOV = 1 << 15;
    public static final int CLFSH = 1 << 19;
    public static final int FXSR = 1 << 24;
    public static final int SSE = 1 << 25;
    public static final int SSE2 = 1 << 26;

    // FN=1: ECX
    public static final int SSE3 = 1;
    public static final int SSE41 = 1 << 19;
    public static final int SSE42 = 1 << 20;
    public static final int POPCNT = 1 << 23;
    public static final int XSAVE = 1 << 26;
    public static final int OXSAVE = 1 << 27;
    public static final int RDRND = 1 << 30;

    // FN=7/0: EBX
    public static final int RDSEED = 1 << 18;

    // FN=80000001h: EDX
    public static final int LM = 1 << 29;

    // FN=80000001h: ECX
    public static final int LAHF = 1;

    public static int[] getI32(String s, int len) {
        CompilerAsserts.neverPartOfCompilation();
        int[] i32 = new int[len];
        for (int i = 0; i < len; i++) {
            byte b1 = getI8(s, i * 4);
            byte b2 = getI8(s, i * 4 + 1);
            byte b3 = getI8(s, i * 4 + 2);
            byte b4 = getI8(s, i * 4 + 3);
            i32[i] = Byte.toUnsignedInt(b1) | Byte.toUnsignedInt(b2) << 8 | Byte.toUnsignedInt(b3) << 16 | Byte.toUnsignedInt(b4) << 24;
        }
        return i32;
    }

    public static byte getI8(String s, int offset) {
        CompilerAsserts.neverPartOfCompilation();
        if (offset >= s.length()) {
            return 0;
        } else {
            return (byte) s.charAt(offset);
        }
    }

    public static int getProcessorInfo(int type, int family, int model, int stepping) {
        CompilerAsserts.neverPartOfCompilation();
        int cpuidFamily = family & 0x0F;
        int cpuidModel = model & 0x0F;
        int cpuidStepping = stepping & 0x0F;
        int cpuidProcessorType = type & 0x0F;
        int cpuidExtendedModel = 0;
        int cpuidExtendedFamily = 0;

        if (model > 15) {
            cpuidExtendedModel = (model >> 4) & 0x0F;
            if (family != 6 && family < 16) {
                throw new IllegalArgumentException("model number too big for given family");
            }
        }
        if (family > 15) {
            cpuidFamily = 15;
            cpuidExtendedFamily = family - 15;
        }
        return cpuidStepping | (cpuidModel << 4) | (cpuidFamily << 8) | (cpuidProcessorType << 12) | (cpuidExtendedModel << 16) | (cpuidExtendedFamily << 20);
    }
}
