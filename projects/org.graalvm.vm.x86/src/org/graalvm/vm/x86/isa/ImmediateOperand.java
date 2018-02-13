package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ImmediateNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class ImmediateOperand extends Operand {
    private final long value;
    private final int size;

    public ImmediateOperand(byte value) {
        this.value = value;
        this.size = 1;
    }

    public ImmediateOperand(short value) {
        this.value = value;
        this.size = 2;
    }

    public ImmediateOperand(int value) {
        this.value = value;
        this.size = 4;
    }

    public ImmediateOperand(long value) {
        this.value = value;
        this.size = 8;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("0x%x", value);
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        return new ImmediateNode(value);
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        throw new UnsupportedOperationException("cannot create write node for an immediate");
    }

    @Override
    public int getSize() {
        return size;
    }
}
