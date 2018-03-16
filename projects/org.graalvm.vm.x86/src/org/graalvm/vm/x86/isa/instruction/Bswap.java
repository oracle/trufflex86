package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Bswap extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Bswap(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand.createRead(state, next());
            writeDst = operand.createWrite(state, next());
        }
    }

    public static class Bswapl extends Bswap {
        public Bswapl(long pc, byte[] instruction, Operand operand) {
            super(pc, instruction, operand);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int src = readSrc.executeI32(frame);
            int dst = Integer.reverseBytes(src);
            writeDst.executeI32(frame, dst);
            return next();
        }
    }

    public static class Bswapq extends Bswap {
        public Bswapq(long pc, byte[] instruction, Operand operand) {
            super(pc, instruction, operand);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long src = readSrc.executeI64(frame);
            long dst = Long.reverseBytes(src);
            writeDst.executeI64(frame, dst);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"bswap", operand.toString()};
    }
}
