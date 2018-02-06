package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Inc extends AMD64Instruction {
    private final Operand operand;

    protected Inc(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    public static class Incb extends Inc {
        public Incb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }
    }

    public static class Incw extends Inc {
        public Incw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }
    }

    public static class Incl extends Inc {
        public Incl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }
    }

    @Override
    protected long executeInstruction(VirtualFrame frame) {
        return 0;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"inc", operand.toString()};
    }
}
