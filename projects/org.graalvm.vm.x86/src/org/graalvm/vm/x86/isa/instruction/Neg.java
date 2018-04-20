package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Neg extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode read;
    @Child protected WriteNode write;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected Neg(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createChildren() {
        assert read == null;
        assert write == null;

        CompilerDirectives.transferToInterpreterAndInvalidate();
        ArchitecturalState state = getContextReference().get().getState();
        RegisterAccessFactory regs = state.getRegisters();
        read = operand.createRead(state, next());
        write = operand.createWrite(state, next());
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writePF = regs.getPF().createWrite();
        writeAF = regs.getAF().createWrite();
    }

    public static class Negb extends Neg {
        public Negb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            byte val = read.executeI8(frame);
            byte result = (byte) -val;
            write.executeI8(frame, result);

            boolean overflow = (byte) (val & (0 ^ result)) < 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeCF.execute(frame, val != 0);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Negw extends Neg {
        public Negw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            short val = read.executeI16(frame);
            short result = (short) -val;
            write.executeI16(frame, result);

            boolean overflow = (short) (val & (0 ^ result)) < 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeCF.execute(frame, val != 0);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Negl extends Neg {
        public Negl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            int val = read.executeI32(frame);
            int result = -val;
            write.executeI32(frame, result);

            boolean overflow = (val & (0 ^ result)) < 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeCF.execute(frame, val != 0);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Negq extends Neg {
        public Negq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (read == null) {
                createChildren();
            }
            long val = read.executeI64(frame);
            long result = -val;
            write.executeI64(frame, result);

            boolean overflow = (val & (0 ^ result)) < 0;
            boolean adjust = ((val ^ result) & 0x10) != 0;

            writeCF.execute(frame, val != 0);
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
        return new String[]{"neg", operand.toString()};
    }
}
