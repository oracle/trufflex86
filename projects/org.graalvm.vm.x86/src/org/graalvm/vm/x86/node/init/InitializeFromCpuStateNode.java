package org.graalvm.vm.x86.node.init;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AVXRegister;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class InitializeFromCpuStateNode extends AMD64Node {
    @Child private RegisterWriteNode rax;
    @Child private RegisterWriteNode rbx;
    @Child private RegisterWriteNode rcx;
    @Child private RegisterWriteNode rdx;
    @Child private RegisterWriteNode rsi;
    @Child private RegisterWriteNode rdi;
    @Child private RegisterWriteNode rbp;
    @Child private RegisterWriteNode rsp;
    @Child private RegisterWriteNode r8;
    @Child private RegisterWriteNode r9;
    @Child private RegisterWriteNode r10;
    @Child private RegisterWriteNode r11;
    @Child private RegisterWriteNode r12;
    @Child private RegisterWriteNode r13;
    @Child private RegisterWriteNode r14;
    @Child private RegisterWriteNode r15;
    @Children private AVXRegisterWriteNode[] zmm;
    @Child private RegisterWriteNode fs;
    @Child private RegisterWriteNode gs;
    @Child private WriteFlagNode cf;
    @Child private WriteFlagNode pf;
    @Child private WriteFlagNode af;
    @Child private WriteFlagNode zf;
    @Child private WriteFlagNode sf;
    @Child private WriteFlagNode df;
    @Child private WriteFlagNode of;
    @Child private WriteFlagNode ac;
    @Child private WriteFlagNode id;
    @Child private RegisterWriteNode pc;

    @CompilationFinal private FrameSlot instructionCount;

    private void createChildrenIfNecessary() {
        if (pc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            rax = regs.getRegister(Register.RAX).createWrite();
            rbx = regs.getRegister(Register.RBX).createWrite();
            rcx = regs.getRegister(Register.RCX).createWrite();
            rdx = regs.getRegister(Register.RDX).createWrite();
            rsi = regs.getRegister(Register.RSI).createWrite();
            rdi = regs.getRegister(Register.RDI).createWrite();
            rbp = regs.getRegister(Register.RBP).createWrite();
            rsp = regs.getRegister(Register.RSP).createWrite();
            r8 = regs.getRegister(Register.R8).createWrite();
            r9 = regs.getRegister(Register.R9).createWrite();
            r10 = regs.getRegister(Register.R10).createWrite();
            r11 = regs.getRegister(Register.R11).createWrite();
            r12 = regs.getRegister(Register.R12).createWrite();
            r13 = regs.getRegister(Register.R13).createWrite();
            r14 = regs.getRegister(Register.R14).createWrite();
            r15 = regs.getRegister(Register.R15).createWrite();
            fs = regs.getFS().createWrite();
            gs = regs.getGS().createWrite();
            cf = regs.getCF().createWrite();
            pf = regs.getPF().createWrite();
            af = regs.getAF().createWrite();
            zf = regs.getZF().createWrite();
            sf = regs.getSF().createWrite();
            df = regs.getDF().createWrite();
            of = regs.getOF().createWrite();
            ac = regs.getAC().createWrite();
            id = regs.getID().createWrite();
            zmm = new AVXRegisterWriteNode[32];
            for (int i = 0; i < zmm.length; i++) {
                AVXRegister reg = regs.getAVXRegister(i);
                zmm[i] = reg.createWrite();
            }
            pc = regs.getPC().createWrite();
            instructionCount = state.getInstructionCount();
        }
    }

    @ExplodeLoop
    public void execute(VirtualFrame frame, CpuState state) {
        createChildrenIfNecessary();
        rax.executeI64(frame, state.rax);
        rbx.executeI64(frame, state.rbx);
        rcx.executeI64(frame, state.rcx);
        rdx.executeI64(frame, state.rdx);
        rsi.executeI64(frame, state.rsi);
        rdi.executeI64(frame, state.rdi);
        rbp.executeI64(frame, state.rbp);
        rsp.executeI64(frame, state.rsp);
        r8.executeI64(frame, state.r8);
        r9.executeI64(frame, state.r9);
        r10.executeI64(frame, state.r10);
        r11.executeI64(frame, state.r11);
        r12.executeI64(frame, state.r12);
        r13.executeI64(frame, state.r13);
        r14.executeI64(frame, state.r14);
        r15.executeI64(frame, state.r15);
        fs.executeI64(frame, state.fs);
        gs.executeI64(frame, state.gs);
        pc.executeI64(frame, state.rip);
        cf.execute(frame, state.cf);
        pf.execute(frame, state.pf);
        af.execute(frame, state.af);
        zf.execute(frame, state.zf);
        sf.execute(frame, state.sf);
        df.execute(frame, state.df);
        of.execute(frame, state.of);
        ac.execute(frame, state.ac);
        id.execute(frame, state.id);
        for (int i = 0; i < 16; i++) {
            zmm[i].executeClear(frame);
            zmm[i].executeI128(frame, state.xmm[i]);
        }
        frame.setLong(instructionCount, state.instructionCount);
    }

    @ExplodeLoop
    public void execute(VirtualFrame frame, CpuState state, boolean[] gprMask, boolean[] avxMask) {
        createChildrenIfNecessary();
        CompilerAsserts.partialEvaluationConstant(gprMask);
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RAX.getID()]);
        if (gprMask[Register.RAX.getID()]) {
            rax.executeI64(frame, state.rax);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RBX.getID()]);
        if (gprMask[Register.RBX.getID()]) {
            rbx.executeI64(frame, state.rbx);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RCX.getID()]);
        if (gprMask[Register.RCX.getID()]) {
            rcx.executeI64(frame, state.rcx);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RDX.getID()]);
        if (gprMask[Register.RDX.getID()]) {
            rdx.executeI64(frame, state.rdx);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RSI.getID()]);
        if (gprMask[Register.RSI.getID()]) {
            rsi.executeI64(frame, state.rsi);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RDI.getID()]);
        if (gprMask[Register.RDI.getID()]) {
            rdi.executeI64(frame, state.rdi);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RBP.getID()]);
        if (gprMask[Register.RBP.getID()]) {
            rbp.executeI64(frame, state.rbp);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.RSP.getID()]);
        if (gprMask[Register.RSP.getID()]) {
            rsp.executeI64(frame, state.rsp);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R8.getID()]);
        if (gprMask[Register.R8.getID()]) {
            r8.executeI64(frame, state.r8);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R9.getID()]);
        if (gprMask[Register.R9.getID()]) {
            r9.executeI64(frame, state.r9);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R10.getID()]);
        if (gprMask[Register.R10.getID()]) {
            r10.executeI64(frame, state.r10);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R11.getID()]);
        if (gprMask[Register.R11.getID()]) {
            r11.executeI64(frame, state.r11);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R12.getID()]);
        if (gprMask[Register.R12.getID()]) {
            r12.executeI64(frame, state.r12);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R13.getID()]);
        if (gprMask[Register.R13.getID()]) {
            r13.executeI64(frame, state.r13);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R14.getID()]);
        if (gprMask[Register.R14.getID()]) {
            r14.executeI64(frame, state.r14);
        }
        CompilerAsserts.partialEvaluationConstant(gprMask[Register.R15.getID()]);
        if (gprMask[Register.R15.getID()]) {
            r15.executeI64(frame, state.r15);
        }
        fs.executeI64(frame, state.fs);
        gs.executeI64(frame, state.gs);
        pc.executeI64(frame, state.rip);
        cf.execute(frame, state.cf);
        pf.execute(frame, state.pf);
        af.execute(frame, state.af);
        zf.execute(frame, state.zf);
        sf.execute(frame, state.sf);
        df.execute(frame, state.df);
        of.execute(frame, state.of);
        ac.execute(frame, state.ac);
        id.execute(frame, state.id);
        CompilerAsserts.partialEvaluationConstant(avxMask);
        for (int i = 0; i < 16; i++) {
            CompilerAsserts.partialEvaluationConstant(avxMask[i]);
            if (avxMask[i]) {
                zmm[i].executeClear(frame);
                zmm[i].executeI128(frame, state.xmm[i]);
            }
        }
        frame.setLong(instructionCount, state.instructionCount);
    }
}
