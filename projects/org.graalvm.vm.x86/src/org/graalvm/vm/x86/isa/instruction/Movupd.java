package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movupd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode src;
    @Child protected WriteNode dst;

    protected Movupd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        src = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
    }

    public static class MovupdToReg extends Movupd {
        public MovupdToReg(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 value = src.executeI128(frame);
            dst.executeI128(frame, value);
            return next();
        }
    }

    public static class MovupdToRM extends Movupd {
        public MovupdToRM(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand1(128), operands.getAVXOperand2(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 value = src.executeI128(frame);
            dst.executeI128(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movupd", operand1.toString(), operand2.toString()};
    }
}
