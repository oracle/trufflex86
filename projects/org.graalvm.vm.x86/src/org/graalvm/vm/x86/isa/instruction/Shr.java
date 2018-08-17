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

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Shr extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected ReadNode readShift;
    @Child protected WriteNode writeDst;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writePF;

    protected Shr(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc = operand1.createRead(state, next());
        readShift = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
        writeCF = state.getRegisters().getCF().createWrite();
        writeOF = state.getRegisters().getOF().createWrite();
        writeZF = state.getRegisters().getZF().createWrite();
        writeSF = state.getRegisters().getSF().createWrite();
        writePF = state.getRegisters().getPF().createWrite();
    }

    public static class Shrb extends Shr {
        public Shrb(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        public Shrb(long pc, byte[] instruction, OperandDecoder decoder, Operand shamt) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R8), shamt);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            byte src = readSrc.executeI8(frame);
            byte shift = (byte) (readShift.executeI8(frame) & 0x1f);
            byte result = (byte) (Byte.toUnsignedInt(src) >>> shift);
            writeDst.executeI8(frame, result);
            if (shift > 0) {
                int bit = 1 << (shift - 1);
                boolean cf = (src & bit) != 0;
                boolean of = src < 0;
                writeCF.execute(frame, cf);
                writeOF.execute(frame, of);
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
                writePF.execute(frame, Flags.getParity(result));
            }
            return next();
        }
    }

    public static class Shrw extends Shr {
        public Shrw(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        public Shrw(long pc, byte[] instruction, OperandDecoder decoder, Operand shamt) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R16), shamt);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short src = readSrc.executeI16(frame);
            short shift = (short) (readShift.executeI8(frame) & 0x1f);
            short result = (short) (Short.toUnsignedInt(src) >>> shift);
            writeDst.executeI16(frame, result);
            if (shift > 0) {
                int bit = 1 << (shift - 1);
                boolean cf = (src & bit) != 0;
                boolean of = src < 0;
                writeCF.execute(frame, cf);
                writeOF.execute(frame, of);
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
                writePF.execute(frame, Flags.getParity((byte) result));
            }
            return next();
        }
    }

    public static class Shrl extends Shr {
        public Shrl(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        public Shrl(long pc, byte[] instruction, OperandDecoder decoder, Operand shamt) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R32), shamt);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int src = readSrc.executeI32(frame);
            int shift = readShift.executeI8(frame) & 0x1f;
            int result = src >>> shift;
            writeDst.executeI32(frame, result);
            if (shift > 0) {
                int bit = 1 << (shift - 1);
                boolean cf = (src & bit) != 0;
                boolean of = src < 0;
                writeCF.execute(frame, cf);
                writeOF.execute(frame, of);
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
                writePF.execute(frame, Flags.getParity((byte) result));
            }
            return next();
        }
    }

    public static class Shrq extends Shr {
        public Shrq(long pc, byte[] instruction, OperandDecoder decoder, byte imm) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        public Shrq(long pc, byte[] instruction, OperandDecoder decoder, Operand shamt) {
            super(pc, instruction, decoder.getOperand1(OperandDecoder.R64), shamt);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long src = readSrc.executeI64(frame);
            long shift = readShift.executeI8(frame) & 0x3f;
            long result = src >>> shift;
            writeDst.executeI64(frame, result);
            if (shift > 0) {
                long bit = 1L << (shift - 1);
                boolean cf = (src & bit) != 0;
                boolean of = src < 0;
                writeCF.execute(frame, cf);
                writeOF.execute(frame, of);
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
                writePF.execute(frame, Flags.getParity((byte) result));
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"shr", operand1.toString(), operand2.toString()};
    }
}
