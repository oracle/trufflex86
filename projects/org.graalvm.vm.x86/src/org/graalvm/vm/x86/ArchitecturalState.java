package org.graalvm.vm.x86;

import org.graalvm.vm.memory.VirtualMemory;

public class ArchitecturalState {
    private final RegisterAccessFactory registerAccess;
    private final VirtualMemory memory;

    public ArchitecturalState(AMD64Context context) {
        registerAccess = new RegisterAccessFactory(context.getGPRs(), context.getPC());
        memory = context.getMemory();
    }

    public RegisterAccessFactory getRegisters() {
        return registerAccess;
    }

    public VirtualMemory getMemory() {
        return memory;
    }
}
