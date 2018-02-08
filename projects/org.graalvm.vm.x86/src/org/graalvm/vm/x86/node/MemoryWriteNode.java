package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.VirtualMemory;

public class MemoryWriteNode extends AMD64Node {
    private final VirtualMemory memory;

    public MemoryWriteNode(VirtualMemory memory) {
        this.memory = memory;
    }

    public void executeI8(long address, byte value) {
        memory.setI8(address, value);
    }

    public void executeI16(long address, short value) {
        memory.setI16(address, value);
    }

    public void executeI32(long address, int value) {
        memory.setI32(address, value);
    }

    public void executeI64(long address, long value) {
        memory.setI64(address, value);
    }
}
