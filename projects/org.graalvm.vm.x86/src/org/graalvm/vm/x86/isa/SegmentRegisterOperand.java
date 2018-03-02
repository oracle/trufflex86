package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class SegmentRegisterOperand extends Operand {
    private final SegmentRegister segment;

    public SegmentRegisterOperand(SegmentRegister segment) {
        this.segment = segment;
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        switch (segment) {
            case FS:
                return state.getRegisters().getFS().createRead();
            case GS:
                return state.getRegisters().getGS().createRead();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        switch (segment) {
            case FS:
                return state.getRegisters().getFS().createWrite();
            case GS:
                return state.getRegisters().getGS().createWrite();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public String toString() {
        return segment.toString().toLowerCase();
    }
}
