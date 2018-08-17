package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Cld extends AMD64Instruction {
    @Child private WriteFlagNode writeDF;

    public Cld(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        writeDF = state.getRegisters().getDF().createWrite();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        writeDF.execute(frame, false);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cld"};
    }
}
