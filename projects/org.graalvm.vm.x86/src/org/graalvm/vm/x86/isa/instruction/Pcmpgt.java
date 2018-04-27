package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Pcmpgt extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;
    private final char type;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeDst;

    protected Pcmpgt(long pc, byte[] instruction, Operand operand1, Operand operand2, char type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.type = type;

        setGPRReadOperands(operand1, operand2);
        setGPRWriteOperands(operand1);
    }

    protected Pcmpgt(long pc, byte[] instruction, OperandDecoder operands, int size, char type) {
        this(pc, instruction, operands.getAVXOperand2(size), operands.getAVXOperand1(size), type);
    }

    protected void createChildrenIfNecessary() {
        if (readOp1 == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            readOp1 = operand1.createRead(state, next());
            readOp2 = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    protected static abstract class Pcmpgt128 extends Pcmpgt {
        protected Pcmpgt128(long pc, byte[] instruction, OperandDecoder operands, char type) {
            super(pc, instruction, operands, 128, type);
        }

        protected abstract Vector128 compute(Vector128 a, Vector128 b);

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            Vector128 a = readOp1.executeI128(frame);
            Vector128 b = readOp2.executeI128(frame);
            Vector128 result = compute(a, b);
            writeDst.executeI128(frame, result);
            return next();
        }
    }

    public static class Pcmpgt128b extends Pcmpgt128 {
        public Pcmpgt128b(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'b');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.gt8(b);
        }
    }

    public static class Pcmpgt128w extends Pcmpgt128 {
        public Pcmpgt128w(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'w');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.gt16(b);
        }
    }

    public static class Pcmpgt128d extends Pcmpgt128 {
        public Pcmpgt128d(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'd');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.gt32(b);
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pcmpgt" + type, operand1.toString(), operand2.toString()};
    }
}
