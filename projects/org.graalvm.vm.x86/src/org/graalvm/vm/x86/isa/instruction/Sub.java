package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Sub extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode srcA;
    @Child protected ReadNode srcB;
    @Child protected WriteNode dst;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected void createChildren() {
        assert srcA == null;
        assert srcB == null;
        assert dst == null;

        CompilerDirectives.transferToInterpreter();
        ArchitecturalState state = getContextReference().get().getState();
        srcA = operand1.createRead(state, next());
        srcB = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
        writeAF = state.getRegisters().getAF().createWrite();
    }

    protected boolean needsChildren() {
        return srcA == null;
    }

    protected Sub(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public static class Subb extends Sub {
        public Subb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        public Subb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            byte a = srcA.executeI8(frame);
            byte b = srcB.executeI8(frame);
            byte result = (byte) (a - b);
            dst.executeI8(frame, result);

            boolean overflow = (byte) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Byte.toUnsignedInt(a) < Byte.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Subw extends Sub {
        public Subw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        public Subw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            short a = srcA.executeI16(frame);
            short b = srcB.executeI16(frame);
            short result = (short) (a - b);
            dst.executeI16(frame, result);

            boolean overflow = (short) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Short.toUnsignedInt(a) < Short.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Subl extends Sub {
        public Subl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        public Subl(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            int a = srcA.executeI32(frame);
            int b = srcB.executeI32(frame);
            int result = a - b;
            dst.executeI32(frame, result);

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Integer.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Subq extends Sub {
        public Subq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        public Subq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            long a = srcA.executeI64(frame);
            long b = srcB.executeI64(frame);
            long result = a - b;
            dst.executeI64(frame, result);

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Long.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"sub", operand1.toString(), operand2.toString()};
    }
}
