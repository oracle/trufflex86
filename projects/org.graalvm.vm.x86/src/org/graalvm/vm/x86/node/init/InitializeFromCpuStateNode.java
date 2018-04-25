package org.graalvm.vm.x86.node.init;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AVXRegister;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteFlagsNode;

import com.oracle.truffle.api.CompilerDirectives;
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
    @Child private WriteFlagsNode flags;
    @Child private RegisterWriteNode pc;

    private void createChildrenIfNecessary() {
        if (flags == null) {
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
            flags = insert(new WriteFlagsNode());
            zmm = new AVXRegisterWriteNode[32];
            for (int i = 0; i < zmm.length; i++) {
                AVXRegister reg = regs.getAVXRegister(i);
                zmm[i] = reg.createWrite();
            }
            pc = regs.getPC().createWrite();
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
        flags.executeI64(frame, state.rfl);
        for (int i = 0; i < zmm.length; i++) {
            zmm[i].executeI512(frame, state.zmm[i]);
        }
    }
}
