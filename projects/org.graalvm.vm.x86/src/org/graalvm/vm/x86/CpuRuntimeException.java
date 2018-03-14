package org.graalvm.vm.x86;

public class CpuRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -9101988695610007358L;

    private final long pc;

    public CpuRuntimeException(long pc, String message) {
        super(message);
        this.pc = pc;
    }

    public CpuRuntimeException(long pc, Throwable cause) {
        super(cause);
        this.pc = pc;
    }

    public long getPC() {
        return pc;
    }

    @Override
    public String toString() {
        return String.format("0x%016x: %s", pc, super.toString());
    }
}
