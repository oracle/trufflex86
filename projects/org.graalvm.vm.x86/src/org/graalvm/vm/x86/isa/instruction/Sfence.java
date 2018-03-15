package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Sfence extends AMD64Instruction {
    public Sfence(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        // TODO: implement?
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"sfence"};
    }
}
