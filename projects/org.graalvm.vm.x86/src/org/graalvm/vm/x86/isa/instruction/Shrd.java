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
import com.oracle.truffle.api.profiles.ConditionProfile;

public abstract class Shrd extends AMD64Instruction {
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

    protected ConditionProfile countGt0Profile = ConditionProfile.createCountingProfile();

    protected Shrd(long pc, byte[] instruction, Operand operand1, Operand operand2, Operand operand3) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;

        setGPRReadOperands(operand1, operand2, operand3);
        setGPRWriteOperands(operand1);
    }

    protected void createChildrenIfNecessary() {
        if (readOperand1 == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
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

    public static class Shrdw extends Shrd {
        public Shrdw(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short src = readOperand1.executeI16(frame);
            short bits = readOperand2.executeI16(frame);
            int count = readOperand3.executeI8(frame) & 0x1F;
            if (countGt0Profile.profile(count > 0)) {
                int shifted = Short.toUnsignedInt(src) >>> count;
                int add = (int) ((bits & mask(count)) << (16 - count));
                short result = (short) (shifted | add);
                writeDst.executeI16(frame, result);

                boolean cf = ((src >>> (count - 1)) & 0x01) != 0;
                writeOF.execute(frame, (result < 0) != (src < 0));
                writeCF.execute(frame, cf);
                writePF.execute(frame, Flags.getParity((byte) result));
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
            }
            return next();
        }
    }

    public static class Shrdl extends Shrd {
        public Shrdl(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int src = readOperand1.executeI32(frame);
            int bits = readOperand2.executeI32(frame);
            int count = readOperand3.executeI8(frame) & 0x1F;
            if (countGt0Profile.profile(count > 0)) {
                int shifted = src >>> count;
                int add = (int) ((bits & mask(count)) << (32 - count));
                int result = shifted | add;
                writeDst.executeI32(frame, result);

                boolean cf = ((src >>> (count - 1)) & 0x01) != 0;
                writeOF.execute(frame, result < 0 != src < 0);
                writeCF.execute(frame, cf);
                writePF.execute(frame, Flags.getParity((byte) result));
                writeZF.execute(frame, result == 0);
                writeSF.execute(frame, result < 0);
            }
            return next();
        }
    }

    public static class Shrdq extends Shrd {
        public Shrdq(long pc, byte[] instruction, OperandDecoder operands, Operand count) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64), count);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long src = readOperand1.executeI64(frame);
            long bits = readOperand2.executeI64(frame);
            int count = readOperand3.executeI8(frame) & 0x3F;
            if (countGt0Profile.profile(count > 0)) {
                long shifted = src >>> count;
                long add = (bits & mask(count)) << (64 - count);
                long result = shifted | add;
                writeDst.executeI64(frame, result);

                boolean cf = ((src >>> (count - 1)) & 0x01) != 0;
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
        return new String[]{"shrd", operand1.toString(), operand2.toString(), operand3.toString()};
    }
}
