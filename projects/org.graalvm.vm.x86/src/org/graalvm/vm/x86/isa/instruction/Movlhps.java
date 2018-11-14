package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Movlhps extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode src;
    @Child protected ReadNode xmm;
    @Child protected WriteNode dst;

    protected Movlhps(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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

    public Movlhps(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long value = src.executeI64(frame);
        Vector128 reg = xmm.executeI128(frame);
        reg.setI64(0, value);
        dst.executeI128(frame, reg);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movlhps", operand1.toString(), operand2.toString()};
    }
}
