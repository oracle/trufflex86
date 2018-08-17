package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Bsf extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeZF;

    protected Bsf(long pc, byte[] instruction, Operand operand1, Operand operand2) {
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
        writeZF = state.getRegisters().getZF().createWrite();
    }

    public static class Bsfw extends Bsf {
        public Bsfw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R16), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short value = readSrc.executeI16(frame);
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = Integer.numberOfTrailingZeros(value);
                writeDst.executeI16(frame, (short) index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Bsfl extends Bsf {
        public Bsfl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int value = readSrc.executeI32(frame);
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = Integer.numberOfTrailingZeros(value);
                writeDst.executeI32(frame, index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Bsfq extends Bsf {
        public Bsfq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long value = readSrc.executeI64(frame);
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = Long.numberOfTrailingZeros(value);
                writeDst.executeI64(frame, index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"bsf", operand1.toString(), operand2.toString()};
    }
}
