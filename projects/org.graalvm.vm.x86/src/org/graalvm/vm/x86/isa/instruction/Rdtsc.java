package org.graalvm.vm.x86.isa.instruction;

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Rdtsc extends AMD64Instruction {
    @Child private WriteNode writeEAX;
    @Child private WriteNode writeEDX;

    @CompilationFinal private FrameSlot insncntslot;
    private static final boolean useInstructionCount = getBoolean(Options.RDTSC_USE_INSTRUCTION_COUNT);

    public Rdtsc(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRWriteOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDX));
    }

    private void createChildrenIfNecessary() {
        if (writeEAX == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            writeEAX = state.getRegisters().getRegister(Register.EAX).createWrite();
            writeEDX = state.getRegisters().getRegister(Register.EDX).createWrite();

            if (useInstructionCount) {
                insncntslot = state.getInstructionCount();
            }
        }
    }

    @TruffleBoundary
    private static long rdtsc() {
        return System.currentTimeMillis();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        long time;
        if (useInstructionCount) {
            time = FrameUtil.getLongSafe(frame, insncntslot);
        } else {
            time = rdtsc();
        }
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
