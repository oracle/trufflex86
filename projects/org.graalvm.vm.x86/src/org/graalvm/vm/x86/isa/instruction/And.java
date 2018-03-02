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

public abstract class And extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOperand1;
    @Child protected ReadNode readOperand2;
    @Child protected WriteNode writeResult;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected And(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary() {
        if (readOperand1 == null) {
            CompilerDirectives.transferToInterpreter();
            assert readOperand1 == null;
            assert readOperand2 == null;
            ArchitecturalState state = getContextReference().get().getState();
            readOperand1 = operand1.createRead(state, next());
            readOperand2 = operand2.createRead(state, next());
            writeResult = operand1.createWrite(state, next());
            writeCF = state.getRegisters().getCF().createWrite();
            writePF = state.getRegisters().getPF().createWrite();
            writeZF = state.getRegisters().getZF().createWrite();
            writeSF = state.getRegisters().getSF().createWrite();
            writeOF = state.getRegisters().getOF().createWrite();
        }
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

    public static class Andb extends And {
        public Andb(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andb(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R8, swap), getOp2(decoder, OperandDecoder.R8, swap));
        }

        public Andb(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Andb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte a = readOperand1.executeI8(frame);
            byte b = readOperand2.executeI8(frame);
            byte val = (byte) (a & b);
            writeResult.executeI8(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity(val));
            return next();
        }
    }

    public static class Andw extends And {
        public Andw(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andw(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R16, swap), getOp2(decoder, OperandDecoder.R16, swap));
        }

        public Andw(long pc, byte[] instruction, OperandDecoder decoder, short imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Andw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short a = readOperand1.executeI16(frame);
            short b = readOperand2.executeI16(frame);
            short val = (short) (a & b);
            writeResult.executeI16(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Andl extends And {
        public Andl(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andl(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R32, swap), getOp2(decoder, OperandDecoder.R32, swap));
        }

        public Andl(long pc, byte[] instruction, OperandDecoder decoder, int imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Andl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int a = readOperand1.executeI32(frame);
            int b = readOperand2.executeI32(frame);
            int val = a & b;
            writeResult.executeI32(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Andq extends And {
        public Andq(long pc, byte[] instruction, OperandDecoder decoder) {
            this(pc, instruction, decoder, false);
        }

        public Andq(long pc, byte[] instruction, OperandDecoder decoder, boolean swap) {
            super(pc, instruction, getOp1(decoder, OperandDecoder.R64, swap), getOp2(decoder, OperandDecoder.R64, swap));
        }

        public Andq(long pc, byte[] instruction, OperandDecoder decoder, long imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Andq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long a = readOperand1.executeI64(frame);
            long b = readOperand2.executeI64(frame);
            long val = a & b;
            writeResult.executeI64(frame, val);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"and", operand1.toString(), operand2.toString()};
    }
}
