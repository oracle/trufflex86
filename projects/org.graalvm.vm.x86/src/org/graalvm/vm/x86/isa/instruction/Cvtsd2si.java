package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cvtsd2si extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cvtsd2si(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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

    public static class Cvtsd2sil extends Cvtsd2si {
        public Cvtsd2sil(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            double value = readSrc.executeF64(frame);
            // TODO: rounding mode
            writeDst.executeI32(frame, (int) value);
            return next();
        }
    }

    public static class Cvtsd2siq extends Cvtsd2si {
        public Cvtsd2siq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            double value = readSrc.executeF64(frame);
            // TODO: rounding mode
            writeDst.executeI64(frame, (long) value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cvtsd2si", operand1.toString(), operand2.toString()};
    }
}
