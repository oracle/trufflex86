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

public abstract class Cmppd extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    private final String name;
    private final byte type;

    @Child protected ReadNode readSrc1;
    @Child protected ReadNode readSrc2;
    @Child protected WriteNode writeDst;

    protected Cmppd(long pc, byte[] instruction, Operand operand1, Operand operand2, String name, byte type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.name = name;
        this.type = type;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static Cmppd create(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        switch (imm & 0x7) {
            case 0:
                return new Cmpeqpd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 1:
                return new Cmpltpd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 2:
                return new Cmplepd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 3:
                return new Cmpunordpd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 5:
                return new Cmpnltpd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 6:
                return new Cmpnlepd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 7:
                return new Cmpordpd(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
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

    public static class Cmpeqpd extends Cmppd {
        protected Cmpeqpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpeqpd", (byte) 0);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.eqF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpltpd extends Cmppd {
        protected Cmpltpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpltpd", (byte) 1);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.ltF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmplepd extends Cmppd {
        protected Cmplepd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmplepd", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.leF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpunordpd extends Cmppd {
        protected Cmpunordpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpunordpd", (byte) 3);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.unorderedF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpnltpd extends Cmppd {
        protected Cmpnltpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnltpd", (byte) 5);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 ge = a.geF64(b);
            writeDst.executeI128(frame, ge);
            return next();
        }
    }

    public static class Cmpnlepd extends Cmppd {
        protected Cmpnlepd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnlepd", (byte) 6);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.gtF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    public static class Cmpordpd extends Cmppd {
        protected Cmpordpd(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpordpd", (byte) 7);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readSrc1.executeI128(frame);
            Vector128 b = readSrc2.executeI128(frame);
            Vector128 le = a.orderedF64(b);
            writeDst.executeI128(frame, le);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        if (name == null) {
            return new String[]{"cmppd", operand1.toString(), operand2.toString(), Byte.toString(type)};
        } else {
            return new String[]{name, operand1.toString(), operand2.toString()};
        }
    }
}
