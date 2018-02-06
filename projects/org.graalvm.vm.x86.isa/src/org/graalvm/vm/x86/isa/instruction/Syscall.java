package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Syscall extends AMD64Instruction {
    public Syscall(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    protected long executeInstruction(VirtualFrame frame) {
        return 0;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"syscall"};
    }
}
