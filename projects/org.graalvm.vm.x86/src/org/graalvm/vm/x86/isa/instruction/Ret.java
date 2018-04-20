package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Ret extends AMD64Instruction {
    @Child private RegisterReadNode readRSP;
    @Child private RegisterWriteNode writeRSP;
    @Child private MemoryReadNode readMemory;

    public Ret(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    private boolean needChildren() {
        return readRSP == null;
    }

    private void createChildren() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        assert readRSP == null;
        assert writeRSP == null;
        assert readMemory == null;

        ArchitecturalState state = getContextReference().get().getState();
        AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
        readRSP = rsp.createRead();
        writeRSP = rsp.createWrite();
        readMemory = state.createMemoryRead();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (needChildren()) {
            createChildren();
        }

        long rsp = readRSP.executeI64(frame);
        long npc = readMemory.executeI64(rsp);
        writeRSP.executeI64(frame, rsp + 8);
        return npc;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"ret"};
    }
}
