package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Not extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode read;
    @Child protected WriteNode write;

    protected Not(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createChildren() {
        assert read == null;
        assert write == null;

        CompilerDirectives.transferToInterpreter();
        ArchitecturalState state = getContextReference().get().getState();
        read = operand.createRead(state, next());
        write = operand.createWrite(state, next());
    }

    public static class Notb extends Not {
        public Notb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            byte val = read.executeI8(frame);
            write.executeI8(frame, (byte) ~val);
            return next();
        }
    }

    public static class Notw extends Not {
        public Notw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            short val = read.executeI16(frame);
            write.executeI16(frame, (short) ~val);
            return next();
        }
    }

    public static class Notl extends Not {
        public Notl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            int val = read.executeI32(frame);
            write.executeI32(frame, ~val);
            return next();
        }
    }

    public static class Notq extends Not {
        public Notq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            long val = read.executeI64(frame);
            write.executeI64(frame, ~val);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"not", operand.toString()};
    }
}
