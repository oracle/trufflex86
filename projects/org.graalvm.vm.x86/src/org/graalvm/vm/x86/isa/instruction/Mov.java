package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Mov extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        write = operand1.createWrite(state, next());
        read = operand2.createRead(state, next());
    }

    protected static Operand getOp1(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand2(type);
        } else {
            return operands.getOperand1(type);
        }
    }

    protected static Operand getOp2(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand1(type);
        } else {
            return operands.getOperand2(type);
        }
    }

    protected Mov(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    public static class Movb extends Mov {
        public Movb(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Movb(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R8, swap), getOp2(operands, OperandDecoder.R8, swap));
        }

        public Movb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Movb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte val = read.executeI8(frame);
            write.executeI8(frame, val);
            return next();
        }
    }

    public static class Movw extends Mov {
        public Movw(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Movw(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R16, swap), getOp2(operands, OperandDecoder.R16, swap));
        }

        public Movw(long pc, byte[] instruction, OperandDecoder operands, short immediate) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(immediate));
        }

        public Movw(long pc, byte[] instruction, Operand register, short immediate) {
            super(pc, instruction, register, new ImmediateOperand(immediate));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short val = read.executeI16(frame);
            write.executeI16(frame, val);
            return next();
        }
    }

    public static class Movl extends Mov {
        public Movl(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Movl(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R32, swap), getOp2(operands, OperandDecoder.R32, swap));
        }

        public Movl(long pc, byte[] instruction, OperandDecoder operands, int immediate) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(immediate));
        }

        public Movl(long pc, byte[] instruction, Operand register, int immediate) {
            super(pc, instruction, register, new ImmediateOperand(immediate));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int val = read.executeI32(frame);
            write.executeI32(frame, val);
            return next();
        }
    }

    public static class Movq extends Mov {
        public Movq(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Movq(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R64, swap), getOp2(operands, OperandDecoder.R64, swap));
        }

        public Movq(long pc, byte[] instruction, OperandDecoder operands, int immediate) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(immediate));
        }

        public Movq(long pc, byte[] instruction, Operand operand, int immediate) {
            super(pc, instruction, operand, new ImmediateOperand(immediate));
        }

        public Movq(long pc, byte[] instruction, Operand operand, long immediate) {
            super(pc, instruction, operand, new ImmediateOperand(immediate));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long val = read.executeI64(frame);
            write.executeI64(frame, val);
            return next();
        }

        @Override
        protected String[] disassemble() {
            if (operand2 instanceof ImmediateOperand && ((ImmediateOperand) operand2).getSize() == 8) {
                return new String[]{"movabs", operand1.toString(), operand2.toString()};
            } else {
                return super.disassemble();
            }
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"mov", operand1.toString(), operand2.toString()};
    }
}
