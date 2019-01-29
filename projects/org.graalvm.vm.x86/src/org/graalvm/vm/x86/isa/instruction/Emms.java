package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Emms extends AMD64Instruction {
    public Emms(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"emms"};
    }
}
