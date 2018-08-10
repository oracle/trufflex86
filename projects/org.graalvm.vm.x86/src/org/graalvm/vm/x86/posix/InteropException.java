package org.graalvm.vm.x86.posix;

@SuppressWarnings("serial")
public class InteropException extends RuntimeException {
    public InteropException() {
    }

    public InteropException(String message) {
        super(message);
    }
}
