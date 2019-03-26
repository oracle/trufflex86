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

import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;
import org.graalvm.vm.x86.isa.CpuState;

public class DeltaCpuStateRecord extends CpuStateRecord {
    public static final int MAGIC = 0x43505531; // CPU1

    private CpuState current;

    private byte[] deltaId;
    private long[] deltaValue;

    public DeltaCpuStateRecord() {
        super(MAGIC);
    }

    public DeltaCpuStateRecord(CpuState lastState, CpuState current) {
        super(MAGIC);
        this.current = current;
        setLastState(lastState);
        computeDelta(current);
    }

    private void computeDelta(CpuState state) {
        // compute number of differences
        CpuState lastState = getLastState();
        int cnt = 0;
        if (lastState.rax != state.rax) {
            cnt++;
        }
        if (lastState.rcx != state.rcx) {
            cnt++;
        }
        if (lastState.rdx != state.rdx) {
            cnt++;
        }
        if (lastState.rbx != state.rbx) {
            cnt++;
        }
        if (lastState.rsp != state.rsp) {
            cnt++;
        }
        if (lastState.rbp != state.rbp) {
            cnt++;
        }
        if (lastState.rsi != state.rsi) {
            cnt++;
        }
        if (lastState.rdi != state.rdi) {
            cnt++;
        }
        if (lastState.r8 != state.r8) {
            cnt++;
        }
        if (lastState.r9 != state.r9) {
            cnt++;
        }
        if (lastState.r10 != state.r10) {
            cnt++;
        }
        if (lastState.r11 != state.r11) {
            cnt++;
        }
        if (lastState.r12 != state.r12) {
            cnt++;
        }
        if (lastState.r13 != state.r13) {
            cnt++;
        }
        if (lastState.r14 != state.r14) {
            cnt++;
        }
        if (lastState.r15 != state.r15) {
            cnt++;
        }
        if (lastState.rip != state.rip) {
            cnt++;
        }
        if (lastState.fs != state.fs) {
            cnt++;
        }
        if (lastState.gs != state.gs) {
            cnt++;
        }
        if (lastState.getRFL() != state.getRFL()) {
            cnt++;
        }
        if (lastState.instructionCount != state.instructionCount) {
            cnt++;
        }
        for (int i = 0; i < 16; i++) {
            if (!lastState.xmm[i].equals(state.xmm[i])) {
                cnt += 2;
            }
        }

        // compute difference data
        deltaId = new byte[cnt];
        deltaValue = new long[cnt];

        int pos = 0;
        if (lastState.rax != state.rax) {
            deltaId[pos] = 0;
            deltaValue[pos] = state.rax;
            pos++;
        }
        if (lastState.rcx != state.rcx) {
            deltaId[pos] = 1;
            deltaValue[pos] = state.rcx;
            pos++;
        }
        if (lastState.rdx != state.rdx) {
            deltaId[pos] = 2;
            deltaValue[pos] = state.rdx;
            pos++;
        }
        if (lastState.rbx != state.rbx) {
            deltaId[pos] = 3;
            deltaValue[pos] = state.rbx;
            pos++;
        }
        if (lastState.rsp != state.rsp) {
            deltaId[pos] = 4;
            deltaValue[pos] = state.rsp;
            pos++;
        }
        if (lastState.rbp != state.rbp) {
            deltaId[pos] = 5;
            deltaValue[pos] = state.rbp;
            pos++;
        }
        if (lastState.rsi != state.rsi) {
            deltaId[pos] = 6;
            deltaValue[pos] = state.rsi;
            pos++;
        }
        if (lastState.rdi != state.rdi) {
            deltaId[pos] = 7;
            deltaValue[pos] = state.rdi;
            pos++;
        }
        if (lastState.r8 != state.r8) {
            deltaId[pos] = 8;
            deltaValue[pos] = state.r8;
            pos++;
        }
        if (lastState.r9 != state.r9) {
            deltaId[pos] = 9;
            deltaValue[pos] = state.r9;
            pos++;
        }
        if (lastState.r10 != state.r10) {
            deltaId[pos] = 10;
            deltaValue[pos] = state.r10;
            pos++;
        }
        if (lastState.r11 != state.r11) {
            deltaId[pos] = 11;
            deltaValue[pos] = state.r11;
            pos++;
        }
        if (lastState.r12 != state.r12) {
            deltaId[pos] = 12;
            deltaValue[pos] = state.r12;
            pos++;
        }
        if (lastState.r13 != state.r13) {
            deltaId[pos] = 13;
            deltaValue[pos] = state.r13;
            pos++;
        }
        if (lastState.r14 != state.r14) {
            deltaId[pos] = 14;
            deltaValue[pos] = state.r14;
            pos++;
        }
        if (lastState.r15 != state.r15) {
            deltaId[pos] = 15;
            deltaValue[pos] = state.r15;
            pos++;
        }
        if (lastState.rip != state.rip) {
            deltaId[pos] = 16;
            deltaValue[pos] = state.rip;
            pos++;
        }
        if (lastState.fs != state.fs) {
            deltaId[pos] = 17;
            deltaValue[pos] = state.fs;
            pos++;
        }
        if (lastState.gs != state.gs) {
            deltaId[pos] = 18;
            deltaValue[pos] = state.gs;
            pos++;
        }
        if (lastState.getRFL() != state.getRFL()) {
            deltaId[pos] = 19;
            deltaValue[pos] = state.getRFL();
            pos++;
        }
        if (lastState.instructionCount != state.instructionCount) {
            deltaId[pos] = 20;
            deltaValue[pos] = state.instructionCount;
            pos++;
        }
        for (int i = 0; i < 16; i++) {
            if (!lastState.xmm[i].equals(state.xmm[i])) {
                deltaId[pos] = (byte) (21 + 2 * i);
                deltaValue[pos] = state.xmm[i].getI64(0);
                pos++;
                deltaId[pos] = (byte) (22 + 2 * i);
                deltaValue[pos] = state.xmm[i].getI64(1);
                pos++;
            }
        }
    }

    @Override
    public CpuState getState() {
        if (current != null) {
            return current;
        } else {
            // compute state
            CpuState state = getLastState().clone();
            for (int i = 0; i < deltaId.length; i++) {
                long val = deltaValue[i];
                switch (deltaId[i]) {
                    case 0:
                        state.rax = val;
                        break;
                    case 1:
                        state.rcx = val;
                        break;
                    case 2:
                        state.rdx = val;
                        break;
                    case 3:
                        state.rbx = val;
                        break;
                    case 4:
                        state.rsp = val;
                        break;
                    case 5:
                        state.rbp = val;
                        break;
                    case 6:
                        state.rsi = val;
                        break;
                    case 7:
                        state.rdi = val;
                        break;
                    case 8:
                        state.r8 = val;
                        break;
                    case 9:
                        state.r9 = val;
                        break;
                    case 10:
                        state.r10 = val;
                        break;
                    case 11:
                        state.r11 = val;
                        break;
                    case 12:
                        state.r12 = val;
                        break;
                    case 13:
                        state.r13 = val;
                        break;
                    case 14:
                        state.r14 = val;
                        break;
                    case 15:
                        state.r15 = val;
                        break;
                    case 16:
                        state.rip = val;
                        break;
                    case 17:
                        state.fs = val;
                        break;
                    case 18:
                        state.gs = val;
                        break;
                    case 19:
                        state.setRFL(val);
                        break;
                    case 20:
                        state.instructionCount = val;
                        break;
                    default:
                        if (deltaId[i] > 20 && deltaId[i] < (21 + 16 * 2)) {
                            int v = deltaId[i] - 21;
                            int reg = v / 2;
                            state.xmm[reg].setI64(v % 2 == 0 ? 0 : 1, val);
                        } else {
                            throw new RuntimeException("unknown id: " + deltaId[i]);
                        }
                }
            }
            current = state;
            return state;
        }
    }

    @Override
    protected int size() {
        return 1 + deltaId.length + 8 * deltaId.length;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        int cnt = in.read8bit();
        deltaId = new byte[cnt];
        deltaValue = new long[cnt];
        for (int i = 0; i < cnt; i++) {
            deltaId[i] = (byte) in.read8bit();
            deltaValue[i] = in.read64bit();
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write8bit((byte) deltaId.length);
        for (int i = 0; i < deltaId.length; i++) {
            out.write8bit(deltaId[i]);
            out.write64bit(deltaValue[i]);
        }
    }

    @Override
    public String toString() {
        return getState().toString();
    }
}
