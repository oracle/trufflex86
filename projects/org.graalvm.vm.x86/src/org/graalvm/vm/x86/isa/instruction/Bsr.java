package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Bsr extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeZF;

    protected Bsr(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
            writeZF = state.getRegisters().getZF().createWrite();
        }
    }

    public static class Bsrw extends Bsr {
        public Bsrw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R16), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int value = Short.toUnsignedInt(readSrc.executeI16(frame));
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = 31 - Integer.numberOfLeadingZeros(value);
                writeDst.executeI16(frame, (short) index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Bsrl extends Bsr {
        public Bsrl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int value = readSrc.executeI32(frame);
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = 31 - Integer.numberOfLeadingZeros(value);
                writeDst.executeI32(frame, index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Bsrq extends Bsr {
        public Bsrq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long value = readSrc.executeI64(frame);
            if (value == 0) {
                writeZF.execute(frame, true);
            } else {
                int index = 63 - Long.numberOfLeadingZeros(value);
                writeDst.executeI64(frame, index);
                writeZF.execute(frame, false);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"bsr", operand1.toString(), operand2.toString()};
    }
}
