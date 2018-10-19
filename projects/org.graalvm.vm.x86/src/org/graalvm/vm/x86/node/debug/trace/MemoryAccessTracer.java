package org.graalvm.vm.x86.node.debug.trace;

import org.graalvm.vm.memory.MemoryAccessListener;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

public class MemoryAccessTracer implements MemoryAccessListener {
    private ExecutionTraceWriter out;

    public MemoryAccessTracer(ExecutionTraceWriter out) {
        this.out = out;
    }

    public void logMemoryRead(long address, int size) {
        out.memoryAccess(address, false, size);
    }

    public void logMemoryRead(long address, int size, long value) {
        out.memoryAccess(address, false, size, value);
    }

    public void logMemoryRead(long address, Vector128 value) {
        out.memoryAccess(address, false, 16, value);
    }

    public void logMemoryRead(long address, Vector256 value) {
        out.memoryAccess(address, false, 32);
    }

    public void logMemoryRead(long address, Vector512 value) {
        out.memoryAccess(address, false, 64);
    }

    public void logMemoryWrite(long address, int size, long value) {
        out.memoryAccess(address, true, size, value);
    }

    public void logMemoryWrite(long address, Vector128 value) {
        out.memoryAccess(address, true, 16, value);
    }

    public void logMemoryWrite(long address, Vector256 value) {
        out.memoryAccess(address, true, 32);
    }

    public void logMemoryWrite(long address, Vector512 value) {
        out.memoryAccess(address, true, 64);
    }
}
