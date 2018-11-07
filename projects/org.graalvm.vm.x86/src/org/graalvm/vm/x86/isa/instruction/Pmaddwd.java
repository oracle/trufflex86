package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Pmaddwd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readA;
    @Child protected ReadNode readB;
    @Child protected WriteNode writeDst;

    protected Pmaddwd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public Pmaddwd(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readA = operand1.createRead(state, next());
        readB = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 a = readA.executeI128(frame);
        Vector128 b = readB.executeI128(frame);
        int result1 = a.getI16(0) * b.getI16(0) + a.getI16(1) * b.getI16(1);
        int result2 = a.getI16(2) * b.getI16(2) + a.getI16(3) * b.getI16(3);
        int result3 = a.getI16(4) * b.getI16(4) + a.getI16(5) * b.getI16(5);
        int result4 = a.getI16(6) * b.getI16(6) + a.getI16(7) * b.getI16(7);
        Vector128 result = new Vector128(result1, result2, result3, result4);
        writeDst.executeI128(frame, result);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pmaddwd", operand1.toString(), operand2.toString()};
    }
}
