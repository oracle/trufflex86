package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmpsd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    private final String name;
    private final byte type;

    @Child protected ReadNode readSrc1;
    @Child protected ReadNode readSrc2;
    @Child protected WriteNode writeDst;

    protected Cmpsd(long pc, byte[] instruction, Operand operand1, Operand operand2, String name, byte type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.name = name;
        this.type = type;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static Cmpsd create(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        switch (imm & 0x7) {
            case 0:
                return new Cmpeqsd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 1:
                return new Cmpltsd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 2:
                return new Cmplesd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 5:
                return new Cmpnltsd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 6:
                return new Cmpnlesd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }
        throw new IllegalInstructionException(pc, instruction, "unknown type " + imm);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readSrc1 = operand1.createRead(state, next());
        readSrc2 = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    public static class Cmpeqsd extends Cmpsd {
        protected Cmpeqsd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpeqsd", (byte) 0);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            double a = dst.getF64(1);
            double b = src.getF64(1);
            dst.setI64(1, a == b ? 0xFFFFFFFFFFFFFFFFL : 0x0000000000000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpltsd extends Cmpsd {
        protected Cmpltsd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpltsd", (byte) 1);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            double a = dst.getF64(1);
            double b = src.getF64(1);
            dst.setI64(1, a < b ? 0xFFFFFFFFFFFFFFFFL : 0x0000000000000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmplesd extends Cmpsd {
        protected Cmplesd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmplesd", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            double a = dst.getF64(1);
            double b = src.getF64(1);
            dst.setI64(1, a <= b ? 0xFFFFFFFFFFFFFFFFL : 0x0000000000000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpnltsd extends Cmpsd {
        protected Cmpnltsd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnltsd", (byte) 5);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            double a = dst.getF64(1);
            double b = src.getF64(1);
            dst.setI64(1, a >= b ? 0xFFFFFFFFFFFFFFFFL : 0x0000000000000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpnlesd extends Cmpsd {
        protected Cmpnlesd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnlesd", (byte) 6);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            double a = dst.getF64(1);
            double b = src.getF64(1);
            dst.setI64(1, a > b ? 0xFFFFFFFFFFFFFFFFL : 0x0000000000000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        if (name == null) {
            return new String[]{"cmpsd", operand1.toString(), operand2.toString(), Byte.toString(type)};
        } else {
            return new String[]{name, operand1.toString(), operand2.toString()};
        }
    }
}
