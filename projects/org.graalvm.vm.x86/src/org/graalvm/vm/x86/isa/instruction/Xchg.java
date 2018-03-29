package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Xchg extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeOp1;
    @Child protected WriteNode writeOp2;

    protected Xchg(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary() {
        if (readOp1 == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readOp1 = operand1.createRead(state, next());
            readOp2 = operand2.createRead(state, next());
            writeOp1 = operand1.createWrite(state, next());
            writeOp2 = operand2.createWrite(state, next());
        }
    }

    public static class Xchgb extends Xchg {
        public Xchgb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R8), operands.getOperand2(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte a = readOp1.executeI8(frame);
            byte b = readOp2.executeI8(frame);
            writeOp1.executeI8(frame, b);
            writeOp2.executeI8(frame, a);
            return next();
        }
    }

    public static class Xchgw extends Xchg {
        public Xchgw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R16), operands.getOperand2(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short a = readOp1.executeI16(frame);
            short b = readOp2.executeI16(frame);
            writeOp1.executeI16(frame, b);
            writeOp2.executeI16(frame, a);
            return next();
        }
    }

    public static class Xchgl extends Xchg {
        public Xchgl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R32), operands.getOperand2(OperandDecoder.R32));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int a = readOp1.executeI32(frame);
            int b = readOp2.executeI32(frame);
            writeOp1.executeI32(frame, b);
            writeOp2.executeI32(frame, a);
            return next();
        }
    }

    public static class Xchgq extends Xchg {
        public Xchgq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand1(OperandDecoder.R64), operands.getOperand2(OperandDecoder.R64));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long a = readOp1.executeI64(frame);
            long b = readOp2.executeI64(frame);
            writeOp1.executeI64(frame, b);
            writeOp2.executeI64(frame, a);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"xchg", operand1.toString(), operand2.toString()};
    }
}
