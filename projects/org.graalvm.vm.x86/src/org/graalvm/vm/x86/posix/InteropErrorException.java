package org.graalvm.vm.x86.posix;

@SuppressWarnings("serial")
public class InteropErrorException extends InteropException {
    public InteropErrorException(String message) {
        super(message);
    }
}
