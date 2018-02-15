package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

public class MemoryReadNode extends AMD64Node {
    private final VirtualMemory memory;

    public MemoryReadNode(VirtualMemory memory) {
        this.memory = memory;
    }

    public byte executeI8(long address) {
        return memory.getI8(address);
    }

    public short executeI16(long address) {
        return memory.getI16(address);
    }

    public int executeI32(long address) {
        return memory.getI32(address);
    }

    public long executeI64(long address) {
        return memory.getI64(address);
    }

    public Vector128 executeI128(long address) {
        return memory.getI128(address);
    }

    public Vector256 executeI256(long address) {
        return memory.getI256(address);
    }

    public Vector512 executeI512(long address) {
        return memory.getI512(address);
    }
}
