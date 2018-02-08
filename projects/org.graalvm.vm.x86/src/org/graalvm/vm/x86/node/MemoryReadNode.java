package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.VirtualMemory;

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
}
