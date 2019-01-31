package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Cvtps2dq extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cvtps2dq(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
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

    public Cvtps2dq(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        Vector128 src = readSrc.executeI128(frame);
        float f0 = src.getF32(0);
        float f1 = src.getF32(1);
        float f2 = src.getF32(2);
        float f3 = src.getF32(3);
        // TODO: rounding mode
        Vector128 dst = new Vector128((int) f0, (int) f1, (int) f2, (int) f3);
        writeDst.executeI128(frame, dst);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cvtps2dq", operand1.toString(), operand2.toString()};
    }
}
