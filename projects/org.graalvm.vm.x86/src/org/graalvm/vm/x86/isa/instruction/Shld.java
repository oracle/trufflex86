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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

public abstract class Shld extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;
    private final Operand operand3;

    @Child protected ReadNode readOperand1;
    @Child protected ReadNode readOperand2;
    @Child protected ReadNode readOperand3;
    @Child protected WriteNode writeDst;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected final ConditionProfile countGt0Profile = ConditionProfile.createCountingProfile();

    protected Shld(long pc, byte[] instruction, Operand operand1, Operand operand2, Operand operand3) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;

        setGPRReadOperands(operand1, operand2, operand3);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readOperand1 = operand1.createRead(state, next());
        readOperand2 = operand2.createRead(state, next());
        readOperand3 = operand3.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
        writeCF = regs.getCF().createWrite();
        writePF = regs.getPF().createWrite();
        writeZF = regs.getZF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeOF = regs.getOF().createWrite();
    }

    protected long mask(int n) {
        long result = 0;
        long bit = 1;
        for (int i = 0; i < n; i++) {
            result |= bit;
            bit <<= 1;
        }
        return result;
    }

    public static class Shldw extends Shld {
        public Shldw(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            short src = readOperand1.executeI16(frame);
            short bits = readOperand2.executeI16(frame);
            int count = readOperand3.executeI8(frame) & 0x1F;
            if (countGt0Profile.profile(count > 0)) {
                int shifted = src << count;
                int add = (int) ((bits >> (16 - count)) & mask(count));
                short result = (short) (shifted | add);
                writeDst.executeI16(frame, result);

                boolean cf = ((src >> (16 - count)) & 0x01) != 0;
                writeOF.execute(frame, result < 0 != src < 0);
                writeCF.execute(frame, cf);
                writePF.execute(frame, Flags.getParity((byte) result));
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
            }
            return next();
        }
    }

    public static class Shldl extends Shld {
        public Shldl(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            int src = readOperand1.executeI32(frame);
            int bits = readOperand2.executeI32(frame);
            int count = readOperand3.executeI8(frame) & 0x1F;
            if (countGt0Profile.profile(count > 0)) {
                int shifted = src << count;
                int add = (int) ((bits >> (32 - count)) & mask(count));
                int result = shifted | add;
                writeDst.executeI32(frame, result);

                boolean cf = ((src >> (32 - count)) & 0x01) != 0;
                writeOF.execute(frame, result < 0 != src < 0);
                writeCF.execute(frame, cf);
                writePF.execute(frame, Flags.getParity((byte) result));
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
            }
            return next();
        }
    }

    public static class Shldq extends Shld {
        public Shldq(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long src = readOperand1.executeI64(frame);
            long bits = readOperand2.executeI64(frame);
            int count = readOperand3.executeI8(frame) & 0x3F;
            if (countGt0Profile.profile(count > 0)) {
                long shifted = src << count;
                long add = (bits >> (64 - count)) & mask(count);
                long result = shifted | add;
                writeDst.executeI64(frame, result);

                boolean cf = ((src >> (64 - count)) & 0x01) != 0;
                writeOF.execute(frame, result < 0 != src < 0);
                writeCF.execute(frame, cf);
                writePF.execute(frame, Flags.getParity((byte) result));
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
            }
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"shld", operand1.toString(), operand2.toString(), operand3.toString()};
    }
}
