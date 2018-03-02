package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Rdtsc extends AMD64Instruction {
    @Child private WriteNode writeEAX;
    @Child private WriteNode writeEDX;

    public Rdtsc(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    private void createChildrenIfNecessary() {
        if (writeEAX == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            writeEAX = state.getRegisters().getRegister(Register.EAX).createWrite();
            writeEDX = state.getRegisters().getRegister(Register.EDX).createWrite();
        }
    }

    @TruffleBoundary
    private static long rdtsc() {
        return System.currentTimeMillis();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        long time = rdtsc();
        int high = (int) (time >> 32);
        int low = (int) time;
        writeEAX.executeI32(frame, low);
        writeEDX.executeI32(frame, high);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"rdtsc"};
    }
}
