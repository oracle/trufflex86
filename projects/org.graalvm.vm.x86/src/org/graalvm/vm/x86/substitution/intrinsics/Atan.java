package org.graalvm.vm.x86.substitution.intrinsics;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.AVXRegisterReadNode;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Atan extends AMD64Instruction {
    @Child private RegisterReadNode readRSP;
    @Child private AVXRegisterReadNode readXMM0;
    @Child private RegisterWriteNode writeRSP;
    @Child private AVXRegisterWriteNode writeXMM0;
    @Child private MemoryReadNode readMemory;

    public Atan(long pc, byte[] code) {
        super(pc, code);
        setGPRReadOperands(new RegisterOperand(Register.RSP), new AVXRegisterOperand(0, 128));
        setGPRWriteOperands(new RegisterOperand(Register.RSP), new AVXRegisterOperand(0, 128));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        AMD64Register rsp = regs.getRegister(Register.RSP);
        readRSP = rsp.createRead();
        writeRSP = rsp.createWrite();
        readXMM0 = regs.getAVXRegister(0).createRead();
        writeXMM0 = regs.getAVXRegister(0).createWrite();
        readMemory = state.createMemoryRead();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long rsp = readRSP.executeI64(frame);
        long npc = readMemory.executeI64(rsp);
        writeRSP.executeI64(frame, rsp + 8);
        double x = readXMM0.executeF64(frame);
        double result = Math.atan(x);
        writeXMM0.executeF64(frame, result);
        return npc;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"atan"};
    }
}
