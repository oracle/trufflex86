package org.graalvm.vm.x86.isa;

import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class SegmentationViolationInstruction extends AMD64Instruction {
    private final SegmentationViolation exception;

    public SegmentationViolationInstruction(long pc) {
        super(pc, new byte[0]);
        exception = new SegmentationViolation(pc);
    }

    public SegmentationViolationInstruction(SegmentationViolation e) {
        super(e.getAddress(), new byte[0]);
        exception = e;
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreter();
        throw exception;
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    protected String[] disassemble() {
        return new String[]{String.format("; cannot execute memory at 0x%016x", pc)};
    }
}
