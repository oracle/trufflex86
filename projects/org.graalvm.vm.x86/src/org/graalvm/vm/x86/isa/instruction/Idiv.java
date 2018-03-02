package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.math.LongDivision;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Idiv extends AMD64Instruction {
    private static final String DIV_ZERO = "division by zero";
    private static final String DIV_RANGE = "quotient too large";

    private final Operand operand;

    @Child protected ReadNode readOperand;
    @Child protected ReadNode readA;
    @Child protected ReadNode readD;
    @Child protected WriteNode writeA;
    @Child protected WriteNode writeD;

    protected Idiv(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void create8bitChildrenIfNecessary() {
        if (readOperand == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readOperand = operand.createRead(state, next());
            readA = state.getRegisters().getRegister(Register.AX).createRead();
            writeA = state.getRegisters().getRegister(Register.AL).createWrite();
            writeD = state.getRegisters().getRegister(Register.AH).createWrite();
        }
    }

    protected void createChildrenIfNecessary(Register a, Register d) {
        if (readOperand == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readOperand = operand.createRead(state, next());
            readA = state.getRegisters().getRegister(a).createRead();
            readD = state.getRegisters().getRegister(d).createRead();
            writeA = state.getRegisters().getRegister(a).createWrite();
            writeD = state.getRegisters().getRegister(d).createWrite();
        }
    }

    public static class Idivb extends Idiv {
        public Idivb(long pc, byte[] instruction, OperandDecoder operand) {
            super(pc, instruction, operand.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            create8bitChildrenIfNecessary();
            byte divisor = readOperand.executeI8(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            short dividend = readA.executeI16(frame);
            short quotient = (short) (dividend / divisor);
            byte remainder = (byte) (dividend % divisor);
            if ((byte) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI8(frame, (byte) quotient);
            writeD.executeI8(frame, remainder);
            return next();
        }
    }

    public static class Idivw extends Idiv {
        public Idivw(long pc, byte[] instruction, OperandDecoder operand) {
            super(pc, instruction, operand.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AX, Register.DX);
            short divisor = readOperand.executeI16(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            short dividendLow = readA.executeI16(frame);
            short dividendHigh = readA.executeI16(frame);
            int dividend = (Short.toUnsignedInt(dividendHigh) << 16) | Short.toUnsignedInt(dividendLow);
            int quotient = dividend / divisor;
            short remainder = (short) (dividend % divisor);
            if ((short) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI16(frame, (short) quotient);
            writeD.executeI16(frame, remainder);
            return next();
        }
    }

    public static class Idivl extends Idiv {
        public Idivl(long pc, byte[] instruction, OperandDecoder operand) {
            super(pc, instruction, operand.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.EAX, Register.EDX);
            int divisor = readOperand.executeI32(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            int dividendLow = readA.executeI32(frame);
            int dividendHigh = readA.executeI32(frame);
            long dividend = (Integer.toUnsignedLong(dividendHigh) << 32) | Integer.toUnsignedLong(dividendLow);
            long quotient = dividend / divisor;
            int remainder = (int) (dividend % divisor);
            if ((int) quotient != quotient) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            writeA.executeI32(frame, (int) quotient);
            writeD.executeI32(frame, remainder);
            return next();
        }
    }

    public static class Idivq extends Idiv {
        public Idivq(long pc, byte[] instruction, OperandDecoder operand) {
            super(pc, instruction, operand.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.RAX, Register.RDX);
            long divisor = readOperand.executeI64(frame);
            if (divisor == 0) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_ZERO);
            }
            long dividendLow = readA.executeI64(frame);
            long dividendHigh = readA.executeI64(frame);
            LongDivision.Result result = LongDivision.divs128by64(dividendHigh, dividendLow, divisor);
            if (result.isInvalid()) {
                CompilerDirectives.transferToInterpreter();
                throw new ArithmeticException(DIV_RANGE);
            }
            long quotient = result.quotient;
            long remainder = result.remainder;
            writeA.executeI64(frame, quotient);
            writeD.executeI64(frame, remainder);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"idiv", operand.toString()};
    }
}
