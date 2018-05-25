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

public abstract class Tzcnt extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeCF;

    protected Tzcnt(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand2);
        setGPRWriteOperands(operand1);
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
            writeZF = state.getRegisters().getZF().createWrite();
            writeCF = state.getRegisters().getCF().createWrite();
        }
    }

    public static class Tzcntw extends Tzcnt {
        public Tzcntw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R16), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = readSrc.executeI16(frame);
            if (value == 0) {
                writeDst.executeI16(frame, (short) 16);
                writeCF.execute(frame, true);
                writeZF.execute(frame, false);
            } else {
                int index = Integer.numberOfTrailingZeros(value);
                writeDst.executeI16(frame, (short) index);
                writeZF.execute(frame, index == 0);
                writeCF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Tzcntl extends Tzcnt {
        public Tzcntl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int value = readSrc.executeI32(frame);
            if (value == 0) {
                writeDst.executeI32(frame, 32);
                writeCF.execute(frame, true);
                writeZF.execute(frame, false);
            } else {
                int index = Integer.numberOfTrailingZeros(value);
                writeDst.executeI32(frame, index);
                writeZF.execute(frame, index == 0);
                writeCF.execute(frame, false);
            }
            return next();
        }
    }

    public static class Tzcntq extends Tzcnt {
        public Tzcntq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long value = readSrc.executeI64(frame);
            if (value == 0) {
                writeDst.executeI64(frame, 64);
                writeCF.execute(frame, true);
                writeZF.execute(frame, false);
            } else {
                int index = Long.numberOfTrailingZeros(value);
                writeDst.executeI64(frame, index);
                writeZF.execute(frame, index == 0);
                writeCF.execute(frame, false);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"tzcnt", operand1.toString(), operand2.toString()};
    }
}
