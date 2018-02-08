package org.graalvm.vm.x86;

public class SyscallException extends Exception {
    private static final long serialVersionUID = 6528834022867326832L;

    private final long value;

    public SyscallException(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
