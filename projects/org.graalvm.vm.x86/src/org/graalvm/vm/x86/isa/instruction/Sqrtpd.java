package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Sqrtpd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child private ReadNode readSrc;
    @Child private WriteNode writeDst;

    protected Sqrtpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    public Sqrtpd(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
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
        double f1 = src.getF64(0);
        double f2 = src.getF64(1);
        double r1 = Math.sqrt(f1);
        double r2 = Math.sqrt(f2);
        Vector128 dst = new Vector128(r1, r2);
        writeDst.executeI128(frame, dst);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"sqrtpd", operand1.toString(), operand2.toString()};
    }
}
