package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Rdssp extends AMD64Instruction {
    private final Operand dst;
    private final String name;

    protected Rdssp(long pc, byte[] instruction, String name, Operand dst) {
        super(pc, instruction);
        this.name = name;
        this.dst = dst;
    }

    public static class Rdsspq extends Rdssp {
        public Rdsspq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "rdsspq", operands.getOperand1(OperandDecoder.R64));
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        // TODO: implement?
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, dst.toString()};
    }
}
