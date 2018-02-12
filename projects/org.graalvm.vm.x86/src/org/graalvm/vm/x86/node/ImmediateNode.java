package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ImmediateNode extends ReadNode {
    private final long value;

    public ImmediateNode(long value) {
        this.value = value;
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        return (byte) value;
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        return (short) value;
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        return (int) value;
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        return value;
    }
}
