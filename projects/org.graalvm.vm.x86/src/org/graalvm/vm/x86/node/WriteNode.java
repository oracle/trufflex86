package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class WriteNode extends AMD64Node {
    public abstract void executeI8(VirtualFrame frame, byte value);

    public abstract void executeI16(VirtualFrame frame, short value);

    public abstract void executeI32(VirtualFrame frame, int value);

    public abstract void executeI64(VirtualFrame frame, long value);

    @SuppressWarnings("unused")
    public void executeI128(VirtualFrame frame, Vector128 value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public void executeI256(VirtualFrame frame, Vector256 value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public void executeI512(VirtualFrame frame, Vector512 value) {
        throw new UnsupportedOperationException();
    }
}
