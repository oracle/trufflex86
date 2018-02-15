package org.graalvm.vm.x86;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;

public class ArchitecturalState {
    private final RegisterAccessFactory registerAccess;
    private final VirtualMemory memory;

    public ArchitecturalState(AMD64Context context) {
        registerAccess = new RegisterAccessFactory(context.getGPRs(), context.getZMMs(), context.getPC(), context.getCF(), context.getPF(), context.getAF(), context.getZF(), context.getSF(),
                        context.getDF(), context.getOF());
        memory = context.getMemory();
    }

    public RegisterAccessFactory getRegisters() {
        return registerAccess;
    }

    public VirtualMemory getMemory() {
        return memory;
    }

    public MemoryReadNode createMemoryRead() {
        return new MemoryReadNode(memory);
    }

    public MemoryWriteNode createMemoryWrite() {
        return new MemoryWriteNode(memory);
    }
}
