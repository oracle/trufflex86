package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cvtsi2sd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cvtsi2sd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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

    public static class Cvtsi2sdl extends Cvtsi2sd {
        public Cvtsi2sdl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int value = readSrc.executeI32(frame);
            // TODO: rounding mode
            writeDst.executeF64(frame, value);
            return next();
        }
    }

    public static class Cvtsi2sdq extends Cvtsi2sd {
        public Cvtsi2sdq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readSrc.executeI64(frame);
            // TODO: rounding mode
            writeDst.executeF64(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cvtsi2sd", operand1.toString(), operand2.toString()};
    }
}
