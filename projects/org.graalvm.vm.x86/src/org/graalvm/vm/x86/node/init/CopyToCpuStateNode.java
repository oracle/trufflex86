package org.graalvm.vm.x86.node.init;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class CopyToCpuStateNode extends AMD64Node {
    @Child private ReadNode readRAX;
    @Child private ReadNode readRBX;
    @Child private ReadNode readRCX;
    @Child private ReadNode readRDX;
    @Child private ReadNode readRSI;
    @Child private ReadNode readRDI;
    @Child private ReadNode readRBP;
    @Child private ReadNode readRSP;
    @Child private ReadNode readR8;
    @Child private ReadNode readR9;
    @Child private ReadNode readR10;
    @Child private ReadNode readR11;
    @Child private ReadNode readR12;
    @Child private ReadNode readR13;
    @Child private ReadNode readR14;
    @Child private ReadNode readR15;
    @Child private ReadNode readFS;
    @Child private ReadNode readGS;
    @Child private ReadFlagsNode readFlags;
    @Children private ReadNode[] readZMM;

    @CompilationFinal private FrameSlot instructionCount;

    private void createChildrenIfNecessary() {
        if (readRAX == null) {
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            this.readRAX = regs.getRegister(Register.RAX).createRead();
            this.readRBX = regs.getRegister(Register.RBX).createRead();
            this.readRCX = regs.getRegister(Register.RCX).createRead();
            this.readRDX = regs.getRegister(Register.RDX).createRead();
            this.readRSI = regs.getRegister(Register.RSI).createRead();
            this.readRDI = regs.getRegister(Register.RDI).createRead();
            this.readRBP = regs.getRegister(Register.RBP).createRead();
            this.readRSP = regs.getRegister(Register.RSP).createRead();
            this.readR8 = regs.getRegister(Register.R8).createRead();
            this.readR9 = regs.getRegister(Register.R9).createRead();
            this.readR10 = regs.getRegister(Register.R10).createRead();
            this.readR11 = regs.getRegister(Register.R11).createRead();
            this.readR12 = regs.getRegister(Register.R12).createRead();
            this.readR13 = regs.getRegister(Register.R13).createRead();
            this.readR14 = regs.getRegister(Register.R14).createRead();
            this.readR15 = regs.getRegister(Register.R15).createRead();
            this.readFS = regs.getFS().createRead();
            this.readGS = regs.getGS().createRead();
            this.readFlags = insert(regs.createReadFlags());
            this.readZMM = new ReadNode[32];
            for (int i = 0; i < readZMM.length; i++) {
                readZMM[i] = regs.getAVXRegister(i).createRead();
            }
            instructionCount = state.getInstructionCount();
        }
    }

    @ExplodeLoop
    public CpuState execute(VirtualFrame frame, long pc) {
        createChildrenIfNecessary();
        CpuState state = new CpuState();
        state.rax = readRAX.executeI64(frame);
        state.rbx = readRBX.executeI64(frame);
        state.rcx = readRCX.executeI64(frame);
        state.rdx = readRDX.executeI64(frame);
        state.rsi = readRSI.executeI64(frame);
        state.rdi = readRDI.executeI64(frame);
        state.rbp = readRBP.executeI64(frame);
        state.rsp = readRSP.executeI64(frame);
        state.r8 = readR8.executeI64(frame);
        state.r9 = readR9.executeI64(frame);
        state.r10 = readR10.executeI64(frame);
        state.r11 = readR11.executeI64(frame);
        state.r12 = readR12.executeI64(frame);
        state.r13 = readR13.executeI64(frame);
        state.r14 = readR14.executeI64(frame);
        state.r15 = readR15.executeI64(frame);
        state.fs = readFS.executeI64(frame);
        state.gs = readGS.executeI64(frame);
        state.rip = pc;
        state.rfl = readFlags.executeI64(frame);
        for (int i = 0; i < readZMM.length; i++) {
            state.zmm[i] = readZMM[i].executeI512(frame);
        }
        state.instructionCount = FrameUtil.getLongSafe(frame, instructionCount);
        return state;
    }

    @ExplodeLoop
    public CpuState execute(VirtualFrame frame, long pc, CpuState state, boolean[] gprMask, boolean[] avxMask) {
        createChildrenIfNecessary();
        CompilerAsserts.partialEvaluationConstant(gprMask);
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RAX.getID()]);
        if (gprMask[Register.RAX.getID()]) {
            state.rax = readRAX.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RBX.getID()]);
        if (gprMask[Register.RBX.getID()]) {
            state.rbx = readRBX.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RCX.getID()]);
        if (gprMask[Register.RCX.getID()]) {
            state.rcx = readRCX.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RDX.getID()]);
        if (gprMask[Register.RDX.getID()]) {
            state.rdx = readRDX.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RSI.getID()]);
        if (gprMask[Register.RSI.getID()]) {
            state.rsi = readRSI.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RDI.getID()]);
        if (gprMask[Register.RDI.getID()]) {
            state.rdi = readRDI.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RBP.getID()]);
        if (gprMask[Register.RBP.getID()]) {
            state.rbp = readRBP.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RSP.getID()]);
        if (gprMask[Register.RSP.getID()]) {
            state.rsp = readRSP.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R8.getID()]);
        if (gprMask[Register.R8.getID()]) {
            state.r8 = readR8.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R9.getID()]);
        if (gprMask[Register.R9.getID()]) {
            state.r9 = readR9.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R10.getID()]);
        if (gprMask[Register.R10.getID()]) {
            state.r10 = readR10.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R11.getID()]);
        if (gprMask[Register.R11.getID()]) {
            state.r11 = readR11.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R12.getID()]);
        if (gprMask[Register.R12.getID()]) {
            state.r12 = readR12.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R13.getID()]);
        if (gprMask[Register.R13.getID()]) {
            state.r13 = readR13.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R14.getID()]);
        if (gprMask[Register.R14.getID()]) {
            state.r14 = readR14.executeI64(frame);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R15.getID()]);
        if (gprMask[Register.R15.getID()]) {
            state.r15 = readR15.executeI64(frame);
        }
        state.fs = readFS.executeI64(frame);
        state.gs = readGS.executeI64(frame);
        state.rip = pc;
        state.rfl = readFlags.executeI64(frame);
        CompilerAsserts.partialEvaluationConstant(avxMask);
        for (int i = 0; i < readZMM.length; i++) {
            CompilerAsserts.partialEvaluationConstant(avxMask[i]);
            if (avxMask[i]) {
                state.zmm[i] = readZMM[i].executeI512(frame);
            }
        }
        state.instructionCount = FrameUtil.getLongSafe(frame, instructionCount);
        return state;
    }
}
