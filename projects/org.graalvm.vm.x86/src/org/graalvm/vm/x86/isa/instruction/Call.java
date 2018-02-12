package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.ImmediateOperand;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Call extends AMD64Instruction {
    private final Operand operand;

    protected final long bta;
    @Child protected ReadNode readBTA;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryWriteNode writeMemory;

    protected Call(long pc, byte[] instruction, Operand target, long bta) {
        super(pc, instruction);
        this.operand = target;
        this.bta = bta;
    }

    protected boolean needChildren() {
        return readRSP == null;
    }

    protected void createChildren() {
        assert readRSP == null;
        assert writeRSP == null;
        assert writeMemory == null;

        CompilerDirectives.transferToInterpreter();
        ArchitecturalState state = getContextReference().get().getState();
        AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
        readRSP = rsp.createRead();
        writeRSP = rsp.createWrite();
        writeMemory = state.createMemoryWrite();
    }

    protected abstract long getCallTarget(VirtualFrame frame);

    public static class CallRelative extends Call {
        private static long getBTA(long pc, Operand target) {
            if (target instanceof ImmediateOperand) {
                return pc + (int) ((ImmediateOperand) target).getValue();
            } else {
                return 0;
            }
        }

        public CallRelative(long pc, byte[] instruction, Operand target) {
            super(pc, instruction, target, getBTA(pc + instruction.length, target));
        }

        @Override
        protected long getCallTarget(VirtualFrame frame) {
            if (bta != 0) {
                return bta;
            } else {
                return next() + readBTA.executeI64(frame);
            }
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (needChildren()) {
            createChildren();
        }
        long target = getCallTarget(frame);
        long rsp = readRSP.executeI64(frame);
        rsp -= 8;
        writeMemory.executeI64(rsp, next());
        writeRSP.executeI64(frame, rsp);
        return target;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"call", bta != 0 ? String.format("0x%x", bta) : operand.toString()};
    }
}
