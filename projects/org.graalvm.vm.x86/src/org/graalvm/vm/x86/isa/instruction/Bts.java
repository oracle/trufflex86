package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Bts extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readBase;
    @Child protected ReadNode readOffset;
    @Child protected WriteNode writeResult;
    @Child protected WriteFlagNode writeCF;

    protected Bts(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    protected void createChildrenIfNecessary() {
        if (readBase == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readBase = operand1.createRead(state, next());
            readOffset = operand2.createRead(state, next());
            writeResult = operand1.createWrite(state, next());
            writeCF = state.getRegisters().getCF().createWrite();
        }
    }

    public static class Btsw extends Bts {
        public Btsw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        public Btsw(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short base = readBase.executeI16(frame);
            int bit = readOffset.executeI16(frame) & 0x0f;
            boolean cf = (base & (1 << bit)) != 0;
            writeCF.execute(frame, cf);
            base |= (short) (1 << bit);
            writeResult.executeI16(frame, base);
            return next();
        }
    }

    public static class Btsl extends Bts {
        public Btsl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        public Btsl(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int base = readBase.executeI32(frame);
            int bit = readOffset.executeI32(frame) & 0x1f;
            boolean cf = (base & (1 << bit)) != 0;
            writeCF.execute(frame, cf);
            base |= (1 << bit);
            writeResult.executeI32(frame, base);
            return next();
        }
    }

    public static class Btsq extends Bts {
        public Btsq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        public Btsq(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long base = readBase.executeI64(frame);
            long bit = readOffset.executeI64(frame) & 0x3f;
            boolean cf = (base & (1L << bit)) != 0;
            writeCF.execute(frame, cf);
            base |= (1L << bit);
            writeResult.executeI64(frame, base);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"bts", operand1.toString(), operand2.toString()};
    }
}
