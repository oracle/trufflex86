package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Test extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOperand1;
    @Child protected ReadNode readOperand2;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected Test(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
    }

    @Override
    protected void createChildNodes() {
        assert readOperand1 == null;
        assert readOperand2 == null;

        ArchitecturalState state = getState();
        readOperand1 = operand1.createRead(state, next());
        readOperand2 = operand2.createRead(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
    }

    public static class Testb extends Test {
        public Testb(long pc, byte[] instruction, OperandDecoder decoder) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), decoder.getOperand2(OperandDecoder.R8));
        }

        public Testb(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Testb(long pc, byte[] instruction, Operand operand, byte imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte a = readOperand1.executeI8(frame);
            byte b = readOperand2.executeI8(frame);
            byte val = (byte) (a & b);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity(val));
            return next();
        }
    }

    public static class Testw extends Test {
        public Testw(long pc, byte[] instruction, OperandDecoder decoder) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), decoder.getOperand2(OperandDecoder.R16));
        }

        public Testw(long pc, byte[] instruction, OperandDecoder decoder, short imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Testw(long pc, byte[] instruction, Operand operand, short imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short a = readOperand1.executeI16(frame);
            short b = readOperand2.executeI16(frame);
            short val = (short) (a & b);
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Testl extends Test {
        public Testl(long pc, byte[] instruction, OperandDecoder decoder) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), decoder.getOperand2(OperandDecoder.R32));
        }

        public Testl(long pc, byte[] instruction, OperandDecoder decoder, int imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Testl(long pc, byte[] instruction, Operand operand, int imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int a = readOperand1.executeI32(frame);
            int b = readOperand2.executeI32(frame);
            int val = a & b;
            writeCF.execute(frame, false);
            writeOF.execute(frame, false);
            writeZF.execute(frame, val == 0);
            writeSF.execute(frame, val < 0);
            writePF.execute(frame, Flags.getParity((byte) val));
            return next();
        }
    }

    public static class Testq extends Test {
        public Testq(long pc, byte[] instruction, OperandDecoder decoder) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), decoder.getOperand2(OperandDecoder.R64));
        }

        public Testq(long pc, byte[] instruction, OperandDecoder decoder, long imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Testq(long pc, byte[] instruction, Operand operand, long imm) {
            super(pc, instruction, operand, new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long a = readOperand1.executeI64(frame);
            long b = readOperand2.executeI64(frame);
            long val = a & b;
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
        return new String[]{"test", operand1.toString(), operand2.toString()};
    }
}
