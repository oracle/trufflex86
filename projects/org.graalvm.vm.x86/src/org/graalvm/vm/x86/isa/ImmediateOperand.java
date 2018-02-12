package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ImmediateNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class ImmediateOperand extends Operand {
    private final long value;

    public ImmediateOperand(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("0x%x", value);
    }

    @Override
    public ReadNode createRead(ArchitecturalState state) {
        return new ImmediateNode(value);
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state) {
        throw new UnsupportedOperationException("cannot create write node for an immediate");
    }
}
