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

public abstract class Pcmpeq extends AMD64Instruction {
    protected final Operand operand1;
    protected final Operand operand2;
    private final char type;

    @Child protected ReadNode readOp1;
    @Child protected ReadNode readOp2;
    @Child protected WriteNode writeDst;

    protected Pcmpeq(long pc, byte[] instruction, Operand operand1, Operand operand2, char type) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.type = type;
    }

    protected Pcmpeq(long pc, byte[] instruction, OperandDecoder operands, int size, char type) {
        this(pc, instruction, operands.getAVXOperand2(size), operands.getAVXOperand1(size), type);
    }

    protected void createChildrenIfNecessary() {
        if (readOp1 == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readOp1 = operand1.createRead(state, next());
            readOp2 = operand2.createRead(state, next());
            writeDst = operand1.createWrite(state, next());
        }
    }

    protected static abstract class Pcmpeq128 extends Pcmpeq {
        protected Pcmpeq128(long pc, byte[] instruction, OperandDecoder operands, char type) {
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

    public static class Pcmpeq128b extends Pcmpeq128 {
        public Pcmpeq128b(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'b');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.eq8(b);
        }
    }

    public static class Pcmpeq128w extends Pcmpeq128 {
        public Pcmpeq128w(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'w');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.eq16(b);
        }
    }

    public static class Pcmpeq128d extends Pcmpeq128 {
        public Pcmpeq128d(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, 'd');
        }

        @Override
        protected Vector128 compute(Vector128 a, Vector128 b) {
            return a.eq32(b);
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pcmpeq" + type, operand1.toString(), operand2.toString()};
    }
}
