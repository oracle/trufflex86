package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class WriteNode extends AMD64Node {
    public abstract void executeI8(VirtualFrame frame, byte value);

    public abstract void executeI16(VirtualFrame frame, short value);

    public abstract void executeI32(VirtualFrame frame, int value);

    public abstract void executeI64(VirtualFrame frame, long value);
}
