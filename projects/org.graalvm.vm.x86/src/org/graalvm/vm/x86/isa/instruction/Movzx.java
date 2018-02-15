package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movzx extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected ReadNode src;
    @Child protected WriteNode dst;

    protected Movzx(long pc, byte[] instruction, Operand operand1, Operand operand2) {
        super(pc, instruction);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected void createChildrenIfNecessary() {
        if (src == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            src = operand2.createRead(state, next());
            dst = operand1.createWrite(state, next());
        }
    }

    public static class Movzbw extends Movzx {
        public Movzbw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R16), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte value = src.executeI8(frame);
            dst.executeI16(frame, (short) Byte.toUnsignedInt(value));
            return next();
        }
    }

    public static class Movzbl extends Movzx {
        public Movzbl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte value = src.executeI8(frame);
            dst.executeI32(frame, Byte.toUnsignedInt(value));
            return next();
        }
    }

    public static class Movzbq extends Movzx {
        public Movzbq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte value = src.executeI8(frame);
            dst.executeI64(frame, Byte.toUnsignedLong(value));
            return next();
        }
    }

    public static class Movzwl extends Movzx {
        public Movzwl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R32), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = src.executeI16(frame);
            dst.executeI32(frame, Short.toUnsignedInt(value));
            return next();
        }
    }

    public static class Movzwq extends Movzx {
        public Movzwq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands.getOperand2(OperandDecoder.R64), operands.getOperand1(OperandDecoder.R16));
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = src.executeI16(frame);
            dst.executeI64(frame, Short.toUnsignedLong(value));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"movzx", operand1.toString(), operand2.toString()};
    }
}
