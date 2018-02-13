package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Lea extends AMD64Instruction {
    private final Operand operand1;
    private final Operand operand2;

    @Child protected AddressComputationNode address;
    @Child protected WriteNode dst;

    protected Lea(long pc, byte[] instruction, OperandDecoder operands, int type) {
        super(pc, instruction);
        this.operand1 = operands.getOperand2(type);
        this.operand2 = operands.getOperand1(type);

        assert operand2 instanceof MemoryOperand;
    }

    protected void createChildrenIfNecessary() {
        if (address == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            dst = operand1.createWrite(state, next());
            address = new AddressComputationNode(state, (MemoryOperand) operand2, next());
        }
    }

    public static class Leaw extends Lea {
        public Leaw(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R16);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long addr = address.execute(frame);
            dst.executeI16(frame, (short) addr);
            return next();
        }
    }

    public static class Leal extends Lea {
        public Leal(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R32);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long addr = address.execute(frame);
            dst.executeI32(frame, (int) addr);
            return next();
        }
    }

    public static class Leaq extends Lea {
        public Leaq(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, operands, OperandDecoder.R64);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long addr = address.execute(frame);
            dst.executeI64(frame, addr);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"lea", operand1.toString(), operand2.toString()};
    }
}
