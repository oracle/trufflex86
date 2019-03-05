package org.graalvm.vm.x86.substitution.intrinsics;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Strlen extends AMD64Instruction {
    @Child private RegisterReadNode readRSP;
    @Child private RegisterReadNode readRDI;
    @Child private RegisterWriteNode writeRSP;
    @Child private RegisterWriteNode writeRAX;
    @Child private MemoryReadNode readMemory;

    public Strlen(long pc, byte[] code) {
        super(pc, code);
        setGPRReadOperands(new RegisterOperand(Register.RSP), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RSP), new RegisterOperand(Register.RAX));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        AMD64Register rsp = regs.getRegister(Register.RSP);
        AMD64Register rax = regs.getRegister(Register.RAX);
        AMD64Register rdi = regs.getRegister(Register.RDI);
        readRSP = rsp.createRead();
        readRDI = rdi.createRead();
        writeRSP = rsp.createWrite();
        writeRAX = rax.createWrite();
        readMemory = state.createMemoryRead();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long rsp = readRSP.executeI64(frame);
        long npc = readMemory.executeI64(rsp);
        writeRSP.executeI64(frame, rsp + 8);
        long start = readRDI.executeI64(frame);
        long ptr = start;
        while (readMemory.executeI8(ptr) != 0) {
            ptr++;
        }
        writeRAX.executeI64(frame, ptr - start);
        return npc;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"strlen"};
    }
}
