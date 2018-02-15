package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ReadNode extends AMD64Node {
    public abstract byte executeI8(VirtualFrame frame);

    public abstract short executeI16(VirtualFrame frame);

    public abstract int executeI32(VirtualFrame frame);

    public abstract long executeI64(VirtualFrame frame);

    public Vector128 executeI128(@SuppressWarnings("unused") VirtualFrame frame) {
        throw new UnsupportedOperationException();
    }

    public Vector256 executeI256(@SuppressWarnings("unused") VirtualFrame frame) {
        throw new UnsupportedOperationException();
    }

    public Vector512 executeI512(@SuppressWarnings("unused") VirtualFrame frame) {
        throw new UnsupportedOperationException();
    }
}
