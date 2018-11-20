package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Pextrw extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final int imm;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    protected Pextrw(long pc, byte[] instruction, Operand operand1, Operand operand2, int imm) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.imm = imm;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    public Pextrw(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        this(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getAVXOperand1(128), imm);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 src = readSrc.executeI128(frame);
        int sel = imm & 0x7;
        short result = src.getI16(7 - sel);
        if (writeDst instanceof RegisterWriteNode) {
            writeDst.executeI64(frame, Short.toUnsignedLong(result));
        } else {
            writeDst.executeI16(frame, result);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pextrw", operand1.toString(), operand2.toString(), String.format("0x%x", imm)};
    }
}
