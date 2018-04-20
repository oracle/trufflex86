package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Dec extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    protected Dec(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createChildren() {
        assert read == null;
        assert write == null;

        CompilerDirectives.transferToInterpreterAndInvalidate();
        ArchitecturalState state = getContextReference().get().getState();
        read = operand.createRead(state, next());
        write = operand.createWrite(state, next());
    }

    public static class Decb extends Dec {
        public Decb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            byte val = read.executeI8(frame);
            write.executeI8(frame, (byte) (val - 1));
            return next();
        }
    }

    public static class Decw extends Dec {
        public Decw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            short val = read.executeI16(frame);
            write.executeI16(frame, (short) (val - 1));
            return next();
        }
    }

    public static class Decl extends Dec {
        public Decl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            int val = read.executeI32(frame);
            write.executeI32(frame, val - 1);
            return next();
        }
    }

    public static class Decq extends Dec {
        public Decq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            long val = read.executeI64(frame);
            write.executeI64(frame, val - 1);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"dec", operand.toString()};
    }
}
