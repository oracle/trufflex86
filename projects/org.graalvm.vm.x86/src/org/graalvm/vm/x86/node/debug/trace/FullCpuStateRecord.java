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
package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;
import org.graalvm.vm.x86.isa.CpuState;

public class FullCpuStateRecord extends CpuStateRecord {
    public static final int MAGIC = 0x43505530; // CPU0

    private final CpuState state;

    FullCpuStateRecord() {
        this(new CpuState());
    }

    public FullCpuStateRecord(CpuState state) {
        super(MAGIC);
        this.state = state;
    }

    @Override
    public CpuState getState() {
        return state;
    }

    @Override
    protected int getDataSize() {
        return 21 * 8 + 16 * 16;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        state.rax = in.read64bit();
        state.rcx = in.read64bit();
        state.rdx = in.read64bit();
        state.rbx = in.read64bit();
        state.rsp = in.read64bit();
        state.rbp = in.read64bit();
        state.rsi = in.read64bit();
        state.rdi = in.read64bit();
        state.r8 = in.read64bit();
        state.r9 = in.read64bit();
        state.r10 = in.read64bit();
        state.r11 = in.read64bit();
        state.r12 = in.read64bit();
        state.r13 = in.read64bit();
        state.r14 = in.read64bit();
        state.r15 = in.read64bit();
        state.rip = in.read64bit();
        state.fs = in.read64bit();
        state.gs = in.read64bit();
        long rfl = in.read64bit();
        state.setRFL(rfl);
        state.instructionCount = in.read64bit();
        for (int i = 0; i < 16; i++) {
            long hi = in.read64bit();
            long lo = in.read64bit();
            state.xmm[i] = new Vector128(hi, lo);
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(state.rax);
        out.write64bit(state.rcx);
        out.write64bit(state.rdx);
        out.write64bit(state.rbx);
        out.write64bit(state.rsp);
        out.write64bit(state.rbp);
        out.write64bit(state.rsi);
        out.write64bit(state.rdi);
        out.write64bit(state.r8);
        out.write64bit(state.r9);
        out.write64bit(state.r10);
        out.write64bit(state.r11);
        out.write64bit(state.r12);
        out.write64bit(state.r13);
        out.write64bit(state.r14);
        out.write64bit(state.r15);
        out.write64bit(state.rip);
        out.write64bit(state.fs);
        out.write64bit(state.gs);
        out.write64bit(state.getRFL());
        out.write64bit(state.instructionCount);
        for (int i = 0; i < 16; i++) {
            out.write64bit(state.xmm[i].getI64(0));
            out.write64bit(state.xmm[i].getI64(1));
        }
    }

    @Override
    public String toString() {
        return state.toString();
    }
}
