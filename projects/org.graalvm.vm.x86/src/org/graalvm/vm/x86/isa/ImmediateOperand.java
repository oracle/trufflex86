package org.graalvm.vm.x86.isa;

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
}
