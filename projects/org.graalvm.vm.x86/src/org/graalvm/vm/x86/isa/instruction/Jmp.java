package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Jmp extends AMD64Instruction {
    public Jmp(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    protected abstract String getOperand();

    public static class JmpDirect extends Jmp {
        private final long bta;

        public JmpDirect(long pc, byte[] instruction, int offset) {
            super(pc, instruction);
            this.bta = next() + offset;
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            return bta;
        }

        @Override
        public long[] getBTA() {
            return new long[]{bta};
        }

        @Override
        protected String getOperand() {
            return String.format("0x%x", bta);
        }
    }

    public static class JmpIndirect extends Jmp {
        private final Operand operand;

        @Child private ReadNode readBTA;

        public JmpIndirect(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction);
            operand = operands.getOperand1(OperandDecoder.R64);

            setGPRReadOperands(operand);
        }

        private void createChildrenIfNecessary() {
            if (readBTA == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                ArchitecturalState state = getContextReference().get().getState();
                readBTA = operand.createRead(state, next());
            }
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            return readBTA.executeI64(frame);
        }

        @Override
        protected String getOperand() {
            return operand.toString();
        }
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"jmp", getOperand()};
    }
}
