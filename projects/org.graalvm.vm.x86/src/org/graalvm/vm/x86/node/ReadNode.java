package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ReadNode extends AMD64Node {
    public abstract byte executeI8(VirtualFrame frame);

    public abstract short executeI16(VirtualFrame frame);

    public abstract int executeI32(VirtualFrame frame);

    public abstract long executeI64(VirtualFrame frame);
}
