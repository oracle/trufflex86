package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Jmp extends AMD64Instruction {
    private final long bta;

    public Jmp(long pc, byte[] instruction, int offset) {
        super(pc, instruction);
        this.bta = next() + offset;
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        return bta;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    public long[] getBTA() {
        return new long[]{bta};
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"jmp", String.format("0x%x", bta)};
    }
}
