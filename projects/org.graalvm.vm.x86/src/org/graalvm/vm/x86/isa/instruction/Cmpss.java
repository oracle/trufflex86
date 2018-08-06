package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmpss extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    private final String name;
    private final byte type;

    @Child protected ReadNode readSrc1;
    @Child protected ReadNode readSrc2;
    @Child protected WriteNode writeDst;

    protected Cmpss(long pc, byte[] instruction, Operand operand1, Operand operand2, String name, byte type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.name = name;
        this.type = type;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    public static Cmpss create(long pc, byte[] instruction, OperandDecoder operands, byte imm) {
        switch (imm & 0x7) {
            case 1:
                return new Cmpltss(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 2:
                return new Cmpless(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 5:
                return new Cmpnltss(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
            case 6:
                return new Cmpnless(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128));
        }
        throw new IllegalInstructionException(pc, instruction, "unknown type " + imm);
    }

    protected void createChildrenIfNecessary() {
        if (readSrc1 == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc1 = operand1.createRead(state, next());
            readSrc2 = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    public static class Cmpltss extends Cmpss {
        protected Cmpltss(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpltss", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            float a = dst.getF32(3);
            float b = src.getF32(3);
            dst.setI32(3, a < b ? 0xFFFFFFFF : 0x00000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpless extends Cmpss {
        protected Cmpless(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpless", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            float a = dst.getF32(3);
            float b = src.getF32(3);
            dst.setI32(3, a <= b ? 0xFFFFFFFF : 0x00000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpnltss extends Cmpss {
        protected Cmpnltss(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnltss", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            float a = dst.getF32(3);
            float b = src.getF32(3);
            dst.setI32(3, a >= b ? 0xFFFFFFFF : 0x00000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    public static class Cmpnless extends Cmpss {
        protected Cmpnless(long pc, byte[] instruction, Operand operand1, Operand operand2) {
            super(pc, instruction, operand1, operand2, "cmpnless", (byte) 2);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 dst = readSrc1.executeI128(frame);
            Vector128 src = readSrc2.executeI128(frame);
            float a = dst.getF32(3);
            float b = src.getF32(3);
            dst.setI32(3, a > b ? 0xFFFFFFFF : 0x00000000);
            writeDst.executeI128(frame, dst);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        if (name == null) {
            return new String[]{"cmpss", operand1.toString(), operand2.toString(), Byte.toString(type)};
        } else {
            return new String[]{name, operand1.toString(), operand2.toString()};
        }
    }
}
