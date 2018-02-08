package org.graalvm.vm.memory.exception;

import org.graalvm.vm.memory.Memory;

public class DoubleFreeException extends RuntimeException {
    private static final long serialVersionUID = -4027903412205546876L;

    private Memory mem;

    public DoubleFreeException(Memory mem) {
        this.mem = mem;
    }

    public Memory getMemory() {
        return mem;
    }
}
