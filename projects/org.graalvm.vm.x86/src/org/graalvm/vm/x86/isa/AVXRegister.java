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

import org.graalvm.vm.x86.node.AVXRegisterReadNode;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class AVXRegister {
    public static final int TYPE_ZMM = 0;
    public static final int TYPE_XMM = 1;
    public static final int TYPE_F32 = 2;
    public static final int TYPE_F64 = 3;

    private final FrameSlot zmm;
    private final FrameSlot xmm;
    private final FrameSlot xmmF32;
    private final FrameSlot xmmF64;
    private final FrameSlot xmmType;

    public AVXRegister(FrameSlot zmm, FrameSlot xmm, FrameSlot xmmF32, FrameSlot xmmF64, FrameSlot xmmType) {
        this.zmm = zmm;
        this.xmm = xmm;
        this.xmmF32 = xmmF32;
        this.xmmF64 = xmmF64;
        this.xmmType = xmmType;
    }

    public AVXRegisterReadNode createRead() {
        return new AVXRegisterReadNode(zmm, xmm, xmmF32, xmmF64, xmmType);
    }

    public AVXRegisterWriteNode createWrite() {
        return new AVXRegisterWriteNode(zmm, xmm, xmmF32, xmmF64, xmmType);
    }

    @Override
    public String toString() {
        return zmm.getIdentifier().toString();
    }
}
