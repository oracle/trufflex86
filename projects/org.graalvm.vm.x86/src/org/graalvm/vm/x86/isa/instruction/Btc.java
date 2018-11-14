package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Btc extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readBase;
    @Child protected ReadNode readOffset;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteNode writeResult;

    protected Btc(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readBase = operand1.createRead(state, next());
        readOffset = operand2.createRead(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writeResult = operand1.createWrite(state, next());
    }

    public static class Btcw extends Btc {
        public Btcw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        public Btcw(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short base = readBase.executeI16(frame);
            int bit = readOffset.executeI16(frame) & 0x0f;
            int result = base ^ (1 << bit);
            boolean cf = (base & (1 << bit)) != 0;
            writeCF.execute(frame, cf);
            writeResult.executeI16(frame, (short) result);
            return next();
        }
    }

    public static class Btcl extends Btc {
        public Btcl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        public Btcl(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int base = readBase.executeI32(frame);
            int bit = readOffset.executeI32(frame) & 0x1f;
            int result = base ^ (1 << bit);
            boolean cf = (base & (1 << bit)) != 0;
            writeCF.execute(frame, cf);
            writeResult.executeI32(frame, result);
            return next();
        }
    }

    public static class Btcq extends Btc {
        public Btcq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        public Btcq(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long base = readBase.executeI64(frame);
            long bit = readOffset.executeI64(frame) & 0x3f;
            long result = base ^ (1L << bit);
            boolean cf = (base & (1L << bit)) != 0;
            writeCF.execute(frame, cf);
            writeResult.executeI64(frame, result);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"btc", operand1.toString(), operand2.toString()};
    }
}