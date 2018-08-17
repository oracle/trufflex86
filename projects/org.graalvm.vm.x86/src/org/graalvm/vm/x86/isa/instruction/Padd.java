package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Padd extends AMD64Instruction {
    private final String name;
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readA;
    @Child protected ReadNode readB;
    @Child protected WriteNode writeDst;

    protected Padd(long pc, byte[] instruction, Operand operand1, Operand operand2, String name) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.name = name;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        readA = operand1.createRead(state, next());
        readB = operand2.createRead(state, next());
        writeDst = operand1.createWrite(state, next());
    }

    public static class Paddb extends Padd {
        public Paddb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), "paddb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readA.executeI128(frame);
            Vector128 b = readB.executeI128(frame);
            Vector128 result = a.addPackedI8(b);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class Paddw extends Padd {
        public Paddw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), "paddw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readA.executeI128(frame);
            Vector128 b = readB.executeI128(frame);
            Vector128 result = a.addPackedI16(b);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class Paddd extends Padd {
        public Paddd(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), "paddd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readA.executeI128(frame);
            Vector128 b = readB.executeI128(frame);
            Vector128 result = a.addPackedI32(b);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class Paddq extends Padd {
        public Paddq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getAVXOperand2(128), operands.getAVXOperand1(128), "paddq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            Vector128 a = readA.executeI128(frame);
            Vector128 b = readB.executeI128(frame);
            Vector128 result = a.addPackedI64(b);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, operand1.toString(), operand2.toString()};
    }
}
