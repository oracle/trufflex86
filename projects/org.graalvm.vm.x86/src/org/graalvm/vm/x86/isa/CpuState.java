package org.graalvm.vm.x86.isa;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.util.BitTest;

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

    public Vector128[] xmm = new Vector128[16];
    public Vector512[] zmm = new Vector512[32];

    public long instructionCount;

    private static final long RESERVED = bit(1, true) | bit(Flags.IF, true);

    private static long bit(long shift, boolean value) {
        return value ? (1L << shift) : 0;
    }

    public long getRFL() {
        return bit(Flags.CF, cf) | bit(Flags.PF, pf) | bit(Flags.AF, af) | bit(Flags.ZF, zf) | bit(Flags.SF, sf) | bit(Flags.DF, df) | bit(Flags.OF, of) | bit(Flags.AC, ac) | bit(Flags.ID, id) |
                        RESERVED;
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
}
