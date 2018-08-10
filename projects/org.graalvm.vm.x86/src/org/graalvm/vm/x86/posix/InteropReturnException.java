package org.graalvm.vm.x86.posix;

@SuppressWarnings("serial")
public class InteropReturnException extends InteropException {
    private final long value;

    public InteropReturnException(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
