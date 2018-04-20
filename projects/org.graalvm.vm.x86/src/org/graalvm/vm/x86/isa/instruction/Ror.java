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

public abstract class Ror extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readSrc;
    @Child protected ReadNode readShamt;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;

    protected Ror(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand1.createRead(state, next());
            readShamt = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
            writeCF = state.getRegisters().getCF().createWrite();
            writeOF = state.getRegisters().getOF().createWrite();
        }
    }

    public static class Rorb extends Ror {
        public Rorb(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), op2);
        }

        public Rorb(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int src = readSrc.executeI8(frame) & 0xFF;
            int shift = readShamt.executeI8(frame) & 0x7;
            byte result = (byte) ((src >>> shift) | (src << (8 - shift)));
            writeDst.executeI8(frame, result);
            if (shift > 0) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (shift == 1) {
                boolean of = (result < 0) ^ ((result & 0x40) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorw extends Ror {
        public Rorw(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), op2);
        }

        public Rorw(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int src = readSrc.executeI16(frame) & 0xFFFF;
            int shift = readShamt.executeI8(frame) & 0xF;
            short result = (short) ((src >>> shift) | (src << (16 - shift)));
            writeDst.executeI16(frame, result);
            if (shift > 0) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (shift == 1) {
                boolean of = (result < 0) ^ ((result & 0x4000) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorl extends Ror {
        public Rorl(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), op2);
        }

        public Rorl(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int src = readSrc.executeI32(frame);
            int shift = readShamt.executeI8(frame) & 0x1F;
            int result = (src >>> shift) | (src << (32 - shift));
            writeDst.executeI32(frame, result);
            if (shift > 0) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (shift == 1) {
                boolean of = (result < 0) ^ ((result & 0x40000000) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    public static class Rorq extends Ror {
        public Rorq(long pc, byte[] instruction, OperandDecoder operands, Operand op2) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), op2);
        }

        public Rorq(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), new ImmediateOperand(imm));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long src = readSrc.executeI64(frame);
            int shift = readShamt.executeI8(frame) & 0x3F;
            long result = (src >>> shift) | (src << (64 - shift));
            writeDst.executeI64(frame, result);
            if (shift > 0) {
                boolean cf = ((src >>> (shift - 1)) & 1) != 0;
                writeCF.execute(frame, cf);
            }
            if (shift == 1) {
                boolean of = (result < 0) ^ ((result & 0x4000000000000000L) != 0);
                writeOF.execute(frame, of);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"ror", operand1.toString(), operand2.toString()};
    }
}
