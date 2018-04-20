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

public abstract class Add extends AMD64Instruction {
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

        CompilerDirectives.transferToInterpreterAndInvalidate();
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

    protected static Operand getOp1(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand2(type);
        } else {
            return operands.getOperand1(type);
        }
    }

    protected static Operand getOp2(OperandDecoder operands, int type, boolean swap) {
        if (swap) {
            return operands.getOperand1(type);
        } else {
            return operands.getOperand2(type);
        }
    }

    protected Add(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public static class Addb extends Add {
        public Addb(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Addb(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R8, swap), getOp2(operands, OperandDecoder.R8, swap));
        }

        public Addb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Addb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            byte a = srcA.executeI8(frame);
            byte b = srcB.executeI8(frame);
            byte result = (byte) (a + b);
            dst.executeI8(frame, result);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
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

    public static class Addw extends Add {
        public Addw(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Addw(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R16, swap), getOp2(operands, OperandDecoder.R16, swap));
        }

        public Addw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Addw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            short a = srcA.executeI16(frame);
            short b = srcB.executeI16(frame);
            short result = (short) (a + b);
            dst.executeI16(frame, result);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
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

    public static class Addl extends Add {
        public Addl(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Addl(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R32, swap), getOp2(operands, OperandDecoder.R32, swap));
        }

        public Addl(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Addl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            int a = srcA.executeI32(frame);
            int b = srcB.executeI32(frame);
            int result = a + b;
            dst.executeI32(frame, result);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
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

    public static class Addq extends Add {
        public Addq(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Addq(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R64, swap), getOp2(operands, OperandDecoder.R64, swap));
        }

        public Addq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Addq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            long a = srcA.executeI64(frame);
            long b = srcB.executeI64(frame);
            long result = a + b;
            dst.executeI64(frame, result);

            boolean overflow = (result < 0 && a > 0 && b > 0) || (result >= 0 && a < 0 && b < 0);
            boolean carry = ((a < 0 || b < 0) && result >= 0) || (a < 0 && b < 0);
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
        return new String[]{"add", operand1.toString(), operand2.toString()};
    }
}
