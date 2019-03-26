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

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.HexFormatter;

/*-
    RAX=0000000000000000 RBX=0000000000000000 RCX=0000000000000000 RDX=0000000000000000
    RSI=0000000000000000 RDI=0000000000000000 RBP=0000000000000000 RSP=00000040007ffc60
    R8 =0000000000000000 R9 =0000000000000000 R10=0000000000000000 R11=0000000000000000
    R12=0000000000000000 R13=0000000000000000 R14=0000000000000000 R15=0000000000000000
    RIP=00000000004001bf RFL=00000202 [-------] CPL=3 II=0 A20=1 SMM=0 HLT=0
    ES =0000 0000000000000000 00000000 00000000
    CS =0033 0000000000000000 ffffffff 00effb00 DPL=3 CS64 [-RA]
    SS =002b 0000000000000000 ffffffff 00cff300 DPL=3 DS   [-WA]
    DS =0000 0000000000000000 00000000 00000000
    FS =0000 0000000000000000 00000000 00000000
    GS =0000 0000000000000000 00000000 00000000
    LDT=0000 0000000000000000 0000ffff 00008200 DPL=0 LDT
    TR =0000 0000000000000000 0000ffff 00008b00 DPL=0 TSS64-busy
    GDT=     0000004000802000 0000007f
    IDT=     0000004000801000 000001ff
    CR0=80010001 CR2=0000000000000000 CR3=0000000000000000 CR4=00000220
    DR0=0000000000000000 DR1=0000000000000000 DR2=0000000000000000 DR3=0000000000000000
    DR6=00000000ffff0ff0 DR7=0000000000000400
    CCS=0000000000000000 CCD=0000000000000000 CCO=EFLAGS
    EFER=0000000000000500
*/
public class CpuState {
    public boolean printAVX = false;
    public boolean printSSE = true;

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

    public boolean cf;
    public boolean pf;
    public boolean af;
    public boolean zf;
    public boolean sf;
    public boolean df;
    public boolean of;
    public boolean ac;
    public boolean id;

    public long fs;
    public long gs;

    public final Vector128[] xmm = new Vector128[16];
    public final Vector512[] zmm = new Vector512[32];

    public long instructionCount;

    private static final long RESERVED = bit(1, true) | bit(Flags.IF, true);

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    public long getRFL() {
        return bit(Flags.CF, cf) | bit(Flags.PF, pf) | bit(Flags.AF, af) | bit(Flags.ZF, zf) | bit(Flags.SF, sf) | bit(Flags.DF, df) | bit(Flags.OF, of) | bit(Flags.AC, ac) | bit(Flags.ID, id) |
                        RESERVED;
    }

    public void setRFL(long rfl) {
        cf = BitTest.test(rfl, bit(Flags.CF, true));
        pf = BitTest.test(rfl, bit(Flags.PF, true));
        af = BitTest.test(rfl, bit(Flags.AF, true));
        zf = BitTest.test(rfl, bit(Flags.ZF, true));
        sf = BitTest.test(rfl, bit(Flags.SF, true));
        df = BitTest.test(rfl, bit(Flags.DF, true));
        of = BitTest.test(rfl, bit(Flags.OF, true));
        ac = BitTest.test(rfl, bit(Flags.AC, true));
        id = BitTest.test(rfl, bit(Flags.ID, true));
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
        addSegment(buf, "FS", fs);
        addSegment(buf, "GS", gs);
        if (printAVX) {
            for (int i = 0; i < zmm.length; i++) {
                buf.append("xmm").append(i);
                if (i < 10) {
                    buf.append(" ");
                }
                buf.append("=");
                buf.append(zmm[i].getI128(3));
                buf.append('\n');
            }
        } else if (printSSE) {
            for (int i = 0; i < 16; i++) {
                buf.append("XMM").append(i);
                if (i < 10) {
                    buf.append(' ');
                }
                buf.append('=');
                if (xmm[i] != null) {
                    buf.append(xmm[i].hex());
                } else {
                    buf.append(zmm[i].getI128(3).hex());
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

    @Override
    public CpuState clone() {
        CpuState state = new CpuState();
        state.rax = rax;
        state.rcx = rcx;
        state.rdx = rdx;
        state.rbx = rbx;
        state.rsp = rsp;
        state.rbp = rbp;
        state.rsi = rsi;
        state.rdi = rdi;
        state.r8 = r8;
        state.r9 = r9;
        state.r10 = r10;
        state.r11 = r11;
        state.r12 = r12;
        state.r13 = r13;
        state.r14 = r14;
        state.r15 = r15;
        state.rip = rip;
        state.fs = fs;
        state.gs = gs;
        state.cf = cf;
        state.pf = pf;
        state.af = af;
        state.zf = zf;
        state.sf = sf;
        state.df = df;
        state.of = of;
        state.ac = ac;
        state.id = id;
        state.instructionCount = instructionCount;
        for (int i = 0; i < 16; i++) {
            if (xmm[i] != null) {
                state.xmm[i] = xmm[i].clone();
            }
            if (zmm[i] != null) {
                state.zmm[i] = zmm[i].clone();
            }
        }
        return state;
    }
}
