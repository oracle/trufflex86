package org.graalvm.vm.x86.emu;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.isa.Flags;

import com.everyware.util.BitTest;

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
    public final byte[] xmm_space = new byte[256];

    public boolean printSSE = true;

    private Vector128 getXMM(int i) {
        byte[] data = new byte[16];
        for (int j = 0; j < 16; j++) {
            data[j] = xmm_space[i * 16 + 15 - j];
        }
        return new Vector128(data);
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
}
