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
package org.graalvm.vm.x86.emu;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.Endianess;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Flags;

public class Registers {
    public long rax;
    public long rbx;
    public long rcx;
    public long rdx;
    public long rsi;
    public long rdi;
    public long rbp;
    public long rsp;
    public long r8;
    public long r9;
    public long r10;
    public long r11;
    public long r12;
    public long r13;
    public long r14;
    public long r15;
    public long rip;
    public long rflags;
    public long fs_base;
    public long gs_base;
    public long mxcsr;
    public long fcwd;
    public final byte[] st_space = new byte[128];
    public final byte[] xmm_space = new byte[256];

    public boolean printSSE = true;

    public Vector128 getST(int i) {
        long lo = Endianess.get64bitLE(st_space, 16 * i);
        long hi = Endianess.get64bitLE(st_space, 16 * i + 8);
        return new Vector128(hi, lo);
    }

    public Vector128 getXMM(int i) {
        long lo = Endianess.get64bitLE(xmm_space, 16 * i);
        long hi = Endianess.get64bitLE(xmm_space, 16 * i + 8);
        return new Vector128(hi, lo);
    }

    private long getRFL() {
        return rflags;
    }

    private static StringBuilder formatRegLine(StringBuilder buf, String[] names, long[] values) {
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(names[i]);
            buf.append('=');
            buf.append(HexFormatter.tohex(values[i], 16));
        }
        buf.append('\n');
        return buf;
    }

    private static void addFlag(StringBuilder buf, long rfl, long flag, char name) {
        if (BitTest.test(rfl, 1L << flag)) {
            buf.append(name);
        } else {
            buf.append('-');
        }
    }

    private static void addSegment(StringBuilder buf, String name, long segment) {
        buf.append(name);
        buf.append(" =0000 ");
        buf.append(HexFormatter.tohex(segment, 16));
        buf.append(" 00000000 00000000\n");
    }

    @Override
    public String toString() {
        long rfl = getRFL();
        StringBuilder buf = new StringBuilder();
        formatRegLine(buf, new String[]{"RAX", "RBX", "RCX", "RDX"}, new long[]{rax, rbx, rcx, rdx});
        formatRegLine(buf, new String[]{"RSI", "RDI", "RBP", "RSP"}, new long[]{rsi, rdi, rbp, rsp});
        formatRegLine(buf, new String[]{"R8 ", "R9 ", "R10", "R11"}, new long[]{r8, r9, r10, r11});
        formatRegLine(buf, new String[]{"R12", "R13", "R14", "R15"}, new long[]{r12, r13, r14, r15});
        buf.append("RIP=").append(HexFormatter.tohex(rip, 16));
        buf.append(" RFL=").append(HexFormatter.tohex(rfl, 8));
        buf.append(" [");
        addFlag(buf, rfl, Flags.OF, 'O');
        addFlag(buf, rfl, Flags.DF, 'D');
        addFlag(buf, rfl, Flags.SF, 'S');
        addFlag(buf, rfl, Flags.ZF, 'Z');
        addFlag(buf, rfl, Flags.AF, 'A');
        addFlag(buf, rfl, Flags.PF, 'P');
        addFlag(buf, rfl, Flags.CF, 'C');
        buf.append("]\n");
        addSegment(buf, "FS", fs_base);
        addSegment(buf, "GS", gs_base);
        if (printSSE) {
            for (int i = 0; i < 16; i++) {
                buf.append("XMM").append(i);
                if (i < 10) {
                    buf.append(' ');
                }
                buf.append('=');
                if (getXMM(i) != null) {
                    buf.append(getXMM(i).hex());
                }
                if (i % 2 == 0) {
                    buf.append(' ');
                } else {
                    buf.append('\n');
                }
            }
        }
        return buf.toString();
    }

    public void setST(int i, Vector128 vec) {
        long hi = vec.getI64(0);
        long lo = vec.getI64(1);
        Endianess.set64bitLE(st_space, 16 * i, lo);
        Endianess.set64bitLE(st_space, 16 * i + 8, hi);
    }

    public void setXMM(int i, Vector128 vec) {
        long hi = vec.getI64(0);
        long lo = vec.getI64(1);
        Endianess.set64bitLE(xmm_space, 16 * i, lo);
        Endianess.set64bitLE(xmm_space, 16 * i + 8, hi);
    }

    public CpuState toCpuState() {
        CpuState state = new CpuState();
        state.rax = rax;
        state.rbx = rbx;
        state.rcx = rcx;
        state.rdx = rdx;
        state.rsi = rsi;
        state.rdi = rdi;
        state.rbp = rbp;
        state.rsp = rsp;
        state.r8 = r8;
        state.r9 = r9;
        state.r10 = r10;
        state.r11 = r11;
        state.r12 = r12;
        state.r13 = r13;
        state.r14 = r14;
        state.r15 = r15;
        state.setRFL(rflags);
        state.fs = fs_base;
        state.gs = gs_base;
        for (int i = 0; i < 16; i++) {
            long lo = Endianess.get64bitLE(xmm_space, 16 * i);
            long hi = Endianess.get64bitLE(xmm_space, 16 * i + 8);
            state.xmm[i] = new Vector128(hi, lo);
        }
        return state;
    }
}
