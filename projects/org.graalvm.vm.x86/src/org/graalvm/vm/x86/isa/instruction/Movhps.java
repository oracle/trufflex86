package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movhps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode src;
    @Child protected ReadNode xmm;
    @Child protected WriteNode dst;

    protected Movhps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        xmm = operand1.createRead(state, next());
        src = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
    }

    public static class MovhpsToReg extends Movhps {
        public MovhpsToReg(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = src.executeI64(frame);
            Vector128 reg = xmm.executeI128(frame);
            reg.setI64(0, value);
            dst.executeI128(frame, reg);
            return next();
        }
    }

    public static class MovhpsToMem extends Movhps {
        public MovhpsToMem(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand1(128), operands.getAVXOperand2(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 reg = src.executeI128(frame);
            long value = reg.getI64(0);
            dst.executeI64(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movhps", operand1.toString(), operand2.toString()};
    }
}
