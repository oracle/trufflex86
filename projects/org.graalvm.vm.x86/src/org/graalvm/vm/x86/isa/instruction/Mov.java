package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Mov extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    protected void createChildren() {
        assert read == null;
        assert write == null;

        CompilerDirectives.transferToInterpreter();
        ArchitecturalState state = getContextReference().get().getState();
        write = operand1.createWrite(state, next());
        read = operand2.createRead(state, next());
    }

    protected boolean needsChildren() {
        return read == null;
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
    }

    public static class Movb extends Mov {
        public Movb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        public Movb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
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
            if (needsChildren()) {
                createChildren();
            }
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
            if (needsChildren()) {
                createChildren();
            }
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

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            long val = read.executeI64(frame);
            write.executeI64(frame, val);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"mov", operand1.toString(), operand2.toString()};
    }
}
