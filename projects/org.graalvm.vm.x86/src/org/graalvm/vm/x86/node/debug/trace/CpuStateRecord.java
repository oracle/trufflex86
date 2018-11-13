package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.isa.CpuState;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class CpuStateRecord extends Record {
    public static final int MAGIC = 0x43505530; // CPU0

    private final CpuState state;

    CpuStateRecord() {
        this(new CpuState());
    }

    public CpuStateRecord(CpuState state) {
        super(MAGIC);
        this.state = state;
    }

    public CpuState getState() {
        return state;
    }

    @Override
    protected int size() {
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
