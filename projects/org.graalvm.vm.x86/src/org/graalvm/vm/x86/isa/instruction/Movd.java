package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movd extends AMD64Instruction {
    private final String name;
    protected final Operand operand1;
    protected final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Movd(long pc, byte[] instruction, String name, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.name = name;
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    public static class MovdToReg extends Movd {
        public MovdToReg(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "movd", operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = Integer.toUnsignedLong(readSrc.executeI32(frame));
            Vector128 result = new Vector128(0, value);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class MovdToRM extends Movd {
        public MovdToRM(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "movd", operands.getOperand1(OperandDecoder.R32), operands.getAVXOperand2(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int value = readSrc.executeI32(frame);
            writeDst.executeI32(frame, value);
            return next();
        }
    }

    public static class MovqToReg extends Movd {
        public MovqToReg(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "movq", operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readSrc.executeI64(frame);
            Vector128 result = new Vector128(0, value);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class MovqToRM extends Movd {
        public MovqToRM(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "movq", operands.getOperand1(OperandDecoder.R64), operands.getAVXOperand2(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readSrc.executeI64(frame);
            writeDst.executeI64(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, operand1.toString(), operand2.toString()};
    }
}
