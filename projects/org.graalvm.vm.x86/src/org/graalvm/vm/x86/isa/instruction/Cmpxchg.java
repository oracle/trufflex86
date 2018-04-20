package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmpxchg extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readA;
    @Child protected ReadNode readSrc;
    @Child protected ReadNode readDst;
    @Child protected WriteNode writeA;
    @Child protected WriteNode writeDst;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeOF;

    protected Cmpxchg(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary(int size) {
        if (readA == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            Register a = null;
            switch (size) {
                case OperandDecoder.R8:
                    a = Register.AL;
                    break;
                case OperandDecoder.R16:
                    a = Register.AX;
                    break;
                case OperandDecoder.R32:
                    a = Register.EAX;
                    break;
                case OperandDecoder.R64:
                    a = Register.RAX;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            ArchitecturalState state = getContextReference().get().getState();
            readA = state.getRegisters().getRegister(a).createRead();
            readSrc = operand2.createRead(state, next());
            readDst = operand1.createRead(state, next());
            writeA = state.getRegisters().getRegister(a).createWrite();
            writeDst = operand1.createWrite(state, next());
            writeZF = state.getRegisters().getZF().createWrite();
            writeCF = state.getRegisters().getCF().createWrite();
            writePF = state.getRegisters().getPF().createWrite();
            writeAF = state.getRegisters().getAF().createWrite();
            writeSF = state.getRegisters().getSF().createWrite();
            writeOF = state.getRegisters().getOF().createWrite();
        }
    }

    public static class Cmpxchgb extends Cmpxchg {
        public Cmpxchgb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(OperandDecoder.R8);
            byte al = readA.executeI8(frame);
            byte src = readSrc.executeI8(frame);
            byte dst = readDst.executeI8(frame);
            if (al == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI8(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI8(frame, dst);
                writeDst.executeI8(frame, dst); // always write dst
            }

            byte result = (byte) (al - dst);

            boolean overflow = ((al ^ dst) & (al ^ result)) < 0;
            boolean carry = Byte.toUnsignedInt(al) < Byte.toUnsignedInt(dst);
            boolean adjust = (((al ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgw extends Cmpxchg {
        public Cmpxchgw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(OperandDecoder.R16);
            short ax = readA.executeI16(frame);
            short src = readSrc.executeI16(frame);
            short dst = readDst.executeI16(frame);
            if (ax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI16(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI16(frame, dst);
                writeDst.executeI16(frame, dst); // always write dst
            }

            short result = (short) (ax - dst);

            boolean overflow = ((ax ^ dst) & (ax ^ result)) < 0;
            boolean carry = Short.toUnsignedInt(ax) < Short.toUnsignedInt(dst);
            boolean adjust = (((ax ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgl extends Cmpxchg {
        public Cmpxchgl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(OperandDecoder.R32);
            int eax = readA.executeI32(frame);
            int src = readSrc.executeI32(frame);
            int dst = readDst.executeI32(frame);
            if (eax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI32(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI32(frame, dst);
                writeDst.executeI32(frame, dst); // always write dst
            }

            int result = eax - dst;

            boolean overflow = ((eax ^ dst) & (eax ^ result)) < 0;
            boolean carry = Integer.compareUnsigned(eax, dst) < 0;
            boolean adjust = (((eax ^ dst) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);
            return next();
        }
    }

    public static class Cmpxchgq extends Cmpxchg {
        public Cmpxchgq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(OperandDecoder.R64);
            long rax = readA.executeI64(frame);
            long src = readSrc.executeI64(frame);
            long dst = readDst.executeI64(frame);
            if (rax == dst) {
                writeZF.execute(frame, true);
                writeDst.executeI64(frame, src);
            } else {
                writeZF.execute(frame, false);
                writeA.executeI64(frame, dst);
                writeDst.executeI64(frame, dst); // always write dst
            }

            long result = rax - dst;

            boolean overflow = ((rax ^ dst) & (rax ^ result)) < 0;
            boolean carry = Long.compareUnsigned(rax, dst) < 0;
            boolean adjust = (((rax ^ dst) ^ result) & 0x10) != 0;

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
        return new String[]{"cmpxchg", operand1.toString(), operand2.toString()};
    }
}
