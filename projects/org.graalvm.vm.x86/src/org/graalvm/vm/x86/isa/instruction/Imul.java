package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.everyware.math.LongMultiplication;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Imul extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;
    protected final Operand operand3;

    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writePF;

    protected Imul(long pc, byte[] instruction, Operand operand1) {
        this(pc, instruction, operand1, null, null);
    }

    protected Imul(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        this(pc, instruction, operand1, operand2, null);
    }

    protected Imul(long pc, byte[] instruction, Operand operand1, Operand operand2, Operand operand3) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    protected void createFlagNodes(ArchitecturalState state) {
        RegisterAccessFactory regs = state.getRegisters();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writePF = regs.getPF().createWrite();
    }

    private static abstract class Imul1 extends Imul {
        @Child protected ReadNode readOp;
        @Child protected ReadNode readA;
        @Child protected WriteNode writeA;
        @Child protected WriteNode writeD;

        protected Imul1(long pc, byte[] instruction, OperandDecoder operands, int type) {
            super(pc, instruction, operands.getOperand1(type));
        }

        protected void createChildrenIfNecessary(Register ra, Register wa) {
            createChildrenIfNecessary(ra, wa, null);
        }

        protected void createChildrenIfNecessary(Register ra, Register wa, Register wd) {
            if (readOp == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                ArchitecturalState state = getContextReference().get().getState();
                RegisterAccessFactory regs = state.getRegisters();
                readOp = operand1.createRead(state, next());
                readA = regs.getRegister(ra).createRead();
                writeA = regs.getRegister(wa).createWrite();
                if (wd != null) {
                    writeD = regs.getRegister(wd).createWrite();
                }
                createFlagNodes(state);
            }
        }
    }

    public static class Imul1b extends Imul1 {
        public Imul1b(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R8);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AL, Register.AX);
            byte a = readOp.executeI8(frame);
            byte b = readA.executeI8(frame);
            int result = a * b;
            writeA.executeI16(frame, (short) result);
            boolean overflow = result != (byte) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (byte) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1w extends Imul1 {
        public Imul1w(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AX, Register.AX, Register.DX);
            short a = readOp.executeI16(frame);
            short b = readA.executeI16(frame);
            int result = a * b;
            writeA.executeI16(frame, (short) result);
            writeD.executeI16(frame, (short) (result >> 16));
            boolean overflow = result != (short) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (short) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1l extends Imul1 {
        public Imul1l(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.EAX, Register.EAX, Register.EDX);
            int a = readOp.executeI32(frame);
            int b = readA.executeI32(frame);
            long result = (long) a * (long) b;
            writeA.executeI32(frame, (int) result);
            writeD.executeI32(frame, (int) (result >> 32));
            boolean overflow = result != (int) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (int) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imul1q extends Imul1 {
        public Imul1q(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.RAX, Register.RAX, Register.RDX);
            long a = readOp.executeI64(frame);
            long b = readA.executeI64(frame);
            long resultL = a * b;
            long resultH = LongMultiplication.multiplyHigh(a, b);
            writeA.executeI64(frame, resultL);
            writeD.executeI64(frame, resultH);
            boolean overflow = resultH != 0 && resultH != -1;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, resultL < 0);
            writePF.execute(frame, Flags.getParity((byte) resultL));
            return next();
        }
    }

    private static abstract class Imul2 extends Imul {
        @Child protected ReadNode readOp1;
        @Child protected ReadNode readOp2;
        @Child protected WriteNode writeDst;

        private final Operand srcA;
        private final Operand srcB;
        private final Operand dst;

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type));
            this.dst = operand1;
            this.srcA = operand1;
            this.srcB = operand2;
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, short imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, int imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;
        }

        protected Imul2(long pc, byte[] instruction, OperandDecoder operands, long imm, int type) {
            super(pc, instruction, operands.getOperand2(type), operands.getOperand1(type), new ImmediateOperand(imm));
            this.dst = operand1;
            this.srcA = operand2;
            this.srcB = operand3;
        }

        protected void createChildrenIfNecessary() {
            if (readOp1 == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                ArchitecturalState state = getContextReference().get().getState();
                readOp1 = srcA.createRead(state, next());
                readOp2 = srcB.createRead(state, next());
                writeDst = dst.createWrite(state, next());
                createFlagNodes(state);
            }
        }
    }

    public static class Imulw extends Imul2 {
        public Imulw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        public Imulw(long pc, byte[] instruction, OperandDecoder operands, short imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short op1 = readOp1.executeI16(frame);
            short op2 = readOp2.executeI16(frame);
            int result = op1 * op2;
            writeDst.executeI16(frame, (short) result);
            boolean overflow = result != (short) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (short) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imull extends Imul2 {
        public Imull(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        public Imull(long pc, byte[] instruction, OperandDecoder operands, int imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int op1 = readOp1.executeI32(frame);
            int op2 = readOp2.executeI32(frame);
            long result = (long) op1 * (long) op2;
            writeDst.executeI32(frame, (int) result);
            boolean overflow = result != (int) result;
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, (int) result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    public static class Imulq extends Imul2 {
        public Imulq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        public Imulq(long pc, byte[] instruction, OperandDecoder operands, long imm) {
            super(pc, instruction, operands, imm, OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long op1 = readOp1.executeI64(frame);
            long op2 = readOp2.executeI64(frame);
            long result = op1 * op2;
            writeDst.executeI64(frame, result);
            boolean overflow = op1 != 0 && (result / op1 != op2); // TODO: implement properly!
            writeCF.execute(frame, overflow);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        if (operand3 != null) {
            return new String[]{"imul", operand1.toString(), operand2.toString(), operand3.toString()};
        } else if (operand2 != null) {
            return new String[]{"imul", operand1.toString(), operand2.toString()};
        } else {
            return new String[]{"imul", operand1.toString()};
        }
    }
}
