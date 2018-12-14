package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Clc extends AMD64Instruction {
    @Child private WriteFlagNode writeCF;

    public Clc(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    protected void createChildNodes() {
        AMD64Context ctx = getContext();
        writeCF = ctx.getState().getRegisters().getCF().createWrite();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        writeCF.execute(frame, false);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"clc"};
    }
}
