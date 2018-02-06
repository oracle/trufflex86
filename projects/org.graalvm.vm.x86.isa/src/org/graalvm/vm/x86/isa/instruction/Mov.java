package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Mov extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    protected Mov(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public static class Movb extends Mov {
        public Movb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }
    }

    public static class Movw extends Mov {
        public Movw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        public Movw(long pc, byte[] instruction, Operand register, short immediate) {
            super(pc, instruction, register, new ImmediateOperand(immediate));
        }
    }

    public static class Movl extends Mov {
        public Movl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        public Movl(long pc, byte[] instruction, OperandDecoder operands, int immediate) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(immediate));
        }

        public Movl(long pc, byte[] instruction, Operand register, int immediate) {
            super(pc, instruction, register, new ImmediateOperand(immediate));
        }
    }

    public static class Movq extends Mov {
        public Movq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        public Movq(long pc, byte[] instruction, OperandDecoder operands, int immediate) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(immediate));
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        return 0;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"mov", operand1.toString(), operand2.toString()};
    }
}
