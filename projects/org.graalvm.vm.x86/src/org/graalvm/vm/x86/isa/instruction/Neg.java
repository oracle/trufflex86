package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Neg extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode read;
    @Child protected WriteNode write;
    @Child protected WriteFlagNode writeCF;

    protected Neg(long pc, byte[] instruction, Operand operand) {
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
        writeCF = state.getRegisters().getCF().createWrite();
    }

    public static class Negb extends Neg {
        public Negb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            byte val = read.executeI8(frame);
            write.executeI8(frame, (byte) -val);
            writeCF.execute(frame, val != 0);
            return next();
        }
    }

    public static class Negw extends Neg {
        public Negw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            short val = read.executeI16(frame);
            write.executeI16(frame, (short) -val);
            writeCF.execute(frame, val != 0);
            return next();
        }
    }

    public static class Negl extends Neg {
        public Negl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            int val = read.executeI32(frame);
            write.executeI32(frame, -val);
            writeCF.execute(frame, val != 0);
            return next();
        }
    }

    public static class Negq extends Neg {
        public Negq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            long val = read.executeI64(frame);
            write.executeI64(frame, -val);
            writeCF.execute(frame, val != 0);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"neg", operand.toString()};
    }
}
