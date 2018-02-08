package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Xor extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    protected Xor(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public static class Xorb extends Xor {
        public Xorb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }
    }

    public static class Xorw extends Xor {
        public Xorw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }
    }

    public static class Xorl extends Xor {
        public Xorl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        return 0;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"xor", operand1.toString(), operand2.toString()};
    }
}
