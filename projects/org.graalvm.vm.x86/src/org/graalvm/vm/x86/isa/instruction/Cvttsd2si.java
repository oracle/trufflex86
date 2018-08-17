package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cvttsd2si extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cvttsd2si(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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

    public static class Cvttsd2sil extends Cvttsd2si {
        public Cvttsd2sil(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            double val = readSrc.executeF64(frame);
            // TODO: rounding mode, exceptions
            int ival = (int) val;
            writeDst.executeI32(frame, ival);
            return next();
        }
    }

    public static class Cvttsd2siq extends Cvttsd2si {
        public Cvttsd2siq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getAVXOperand1(128));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            double val = readSrc.executeF64(frame);
            // TODO: rounding mode, exceptions
            long ival = (long) val;
            writeDst.executeI64(frame, ival);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cvttsd2si", operand1.toString(), operand2.toString()};
    }
}
