package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Pinsrw extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final int imm;

    @Child private ReadNode readOp1;
    @Child private ReadNode readOp2;
    @Child private WriteNode writeDst;

    protected Pinsrw(long pc, byte[] instruction, Operand operand1, Operand operand2, int imm) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.imm = imm;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Pinsrw(long pc, byte[] instruction, OperandDecoder operands, int imm) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R32), imm);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readOp1 = operand1.createRead(state, next());
        readOp2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 vec = readOp1.executeI128(frame);
        short word = readOp2.executeI16(frame);
        int sel = imm & 0x3;
        vec.setI16(7 - sel, word);
        writeDst.executeI128(frame, vec);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pinsrw", operand1.toString(), operand2.toString(), String.format("0x%x", imm)};
    }
}
