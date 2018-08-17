package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movsx extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    protected Movsx(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        assert read == null;
        assert write == null;

        ArchitecturalState state = getState();
        write = operand1.createWrite(state, next());
        read = operand2.createRead(state, next());
    }

    public static class Movsbw extends Movsx {
        public Movsbw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R16), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte val = read.executeI8(frame);
            write.executeI16(frame, val);
            return next();
        }
    }

    public static class Movsbl extends Movsx {
        public Movsbl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte val = read.executeI8(frame);
            write.executeI32(frame, val);
            return next();
        }
    }

    public static class Movsbq extends Movsx {
        public Movsbq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte val = read.executeI8(frame);
            write.executeI64(frame, val);
            return next();
        }
    }

    public static class Movswl extends Movsx {
        public Movswl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short val = read.executeI16(frame);
            write.executeI32(frame, val);
            return next();
        }
    }

    public static class Movswq extends Movsx {
        public Movswq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short val = read.executeI16(frame);
            write.executeI64(frame, val);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movsx", operand1.toString(), operand2.toString()};
    }
}
