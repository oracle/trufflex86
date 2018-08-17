package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Movaps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode src;
    @Child protected WriteNode dst;

    protected Movaps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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

    private static Operand getOp1(OperandDecoder operands, int size, boolean swap) {
        return swap ? operands.getAVXOperand2(size) : operands.getAVXOperand1(size);
    }

    private static Operand getOp2(OperandDecoder operands, int size, boolean swap) {
        return swap ? operands.getAVXOperand1(size) : operands.getAVXOperand2(size);
    }

    public Movaps(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands, false);
    }

    public Movaps(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
        this(pc, instruction, getOp2(operands, 128, swap), getOp1(operands, 128, swap));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 value = src.executeI128(frame);
        dst.executeI128(frame, value);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movaps", operand1.toString(), operand2.toString()};
    }
}
