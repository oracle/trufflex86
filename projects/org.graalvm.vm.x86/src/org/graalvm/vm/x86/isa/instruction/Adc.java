package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Adc extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode srcA;
    @Child protected ReadNode srcB;
    @Child protected WriteNode dst;
    @Child protected ReadFlagNode readCF;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;

    protected void createChildren() {
        assert srcA == null;
        assert srcB == null;
        assert dst == null;

        CompilerDirectives.transferToInterpreterAndInvalidate();
        ArchitecturalState state = getContextReference().get().getState();
        srcA = operand1.createRead(state, next());
        srcB = operand2.createRead(state, next());
        dst = operand1.createWrite(state, next());
        RegisterAccessFactory regs = state.getRegisters();
        readCF = regs.getCF().createRead();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writePF = regs.getPF().createWrite();
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

    protected Adc(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static class Adcb extends Adc {
        public Adcb(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcb(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R8, swap), getOp2(operands, OperandDecoder.R8, swap));
        }

        public Adcb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Adcb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            byte a = srcA.executeI8(frame);
            byte b = srcB.executeI8(frame);
            int c = readCF.execute(frame) ? 1 : 0;
            byte result = (byte) (a + b + c);
            byte result1 = (byte) (a + b);
            dst.executeI8(frame, result);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            return next();
        }
    }

    public static class Adcw extends Adc {
        public Adcw(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcw(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R16, swap), getOp2(operands, OperandDecoder.R16, swap));
        }

        public Adcw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Adcw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            short a = srcA.executeI16(frame);
            short b = srcB.executeI16(frame);
            int c = readCF.execute(frame) ? 1 : 0;
            short result1 = (short) (a + b);
            short result = (short) (a + b + c);
            dst.executeI16(frame, result);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Adcl extends Adc {
        public Adcl(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcl(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R32, swap), getOp2(operands, OperandDecoder.R32, swap));
        }

        public Adcl(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Adcl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            int a = srcA.executeI32(frame);
            int b = srcB.executeI32(frame);
            int c = readCF.execute(frame) ? 1 : 0;
            int result = a + b + c;
            int result1 = a + b;
            dst.executeI32(frame, result);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Adcq extends Adc {
        public Adcq(long pc, byte[] instruction, OperandDecoder operands) {
            this(pc, instruction, operands, false);
        }

        public Adcq(long pc, byte[] instruction, OperandDecoder operands, boolean swap) {
            super(pc, instruction, getOp1(operands, OperandDecoder.R64, swap), getOp2(operands, OperandDecoder.R64, swap));
        }

        public Adcq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Adcq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (needsChildren()) {
                createChildren();
            }
            long a = srcA.executeI64(frame);
            long b = srcB.executeI64(frame);
            int c = readCF.execute(frame) ? 1 : 0;
            long result1 = a + b;
            long result = a + b + c;
            dst.executeI64(frame, result);

            boolean overflow = (result1 < 0 && a > 0 && b > 0) || (result1 >= 0 && a < 0 && b < 0);
            overflow |= (result < 0 && result1 > 0 && c > 0);
            overflow &= !(result < 0 && a < 0 && b < 0) && !(result >= 0 && a >= 0 && b >= 0);
            boolean carry = ((a < 0 || b < 0) && result1 >= 0) || (a < 0 && b < 0);
            carry |= ((result1 < 0) && result >= 0);
            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"adc", operand1.toString(), operand2.toString()};
    }
}
