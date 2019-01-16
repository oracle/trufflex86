package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AVXRegisterOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Movhpd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    private final boolean toMemory;

    @Child protected ReadNode readSrc;
    @Child protected ReadNode readDst;
    @Child protected WriteNode writeDst;

    protected Movhpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.toMemory = operand2 instanceof AVXRegisterOperand;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    private static Operand getOp1(OperandDecoder operands, boolean swap) {
        return swap ? operands.getOperand1(OperandDecoder.R64) : operands.getAVXOperand2(128);
    }

    private static Operand getOp2(OperandDecoder operands, boolean swap) {
        return getOp1(operands, !swap);
    }

    public Movhpd(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands, false);
    }

    public Movhpd(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
        this(pc, instruction, getOp1(operands, swap), getOp2(operands, swap));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand2.createRead(state, next());
        readDst = operand1.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (toMemory) {
            Vector128 vec = readSrc.executeI128(frame);
            long value = vec.getI64(0);
            writeDst.executeI64(frame, value);
        } else {
            long value = readSrc.executeI64(frame);
            Vector128 vec = readDst.executeI128(frame);
            vec.setI64(0, value);
            writeDst.executeI128(frame, vec);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movhpd", operand1.toString(), operand2.toString()};
    }
}
