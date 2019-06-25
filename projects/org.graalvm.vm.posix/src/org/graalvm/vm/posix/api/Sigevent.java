package org.graalvm.vm.posix.api;

public class Sigevent implements Struct {
    private static final int SIGEV_PAD_SIZE_32 = 13 * 4;
    private static final int SIGEV_PAD_SIZE_64 = 12 * 4;

    public final Sigval sigev_value = new Sigval();
    public int sigev_signo;
    public int sigev_notify;
    public long _function;
    public long _attribute;

    @Override
    public PosixPointer read32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = sigev_value.read32(p);
        sigev_signo = p.getI32();
        p = p.add(4);
        sigev_notify = p.getI32();
        p = p.add(4);

        PosixPointer union = p;
        _function = p.getI32();
        p = p.add(4);
        _attribute = p.getI32();
        p = p.add(4);
        return union.add(SIGEV_PAD_SIZE_32);
    }

    @Override
    public PosixPointer read64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = sigev_value.read64(p);
        sigev_signo = p.getI32();
        p = p.add(4);
        sigev_notify = p.getI32();
        p = p.add(4);

        PosixPointer union = p;
        _function = p.getI64();
        p = p.add(8);
        _attribute = p.getI64();
        p = p.add(8);
        return union.add(SIGEV_PAD_SIZE_64);
    }

    @Override
    public String toString() {
        return String.format("{sigev_value=%s, sigev_signo=%s, sigev_notify=%s}", sigev_value, Signal.toString(sigev_signo), Signal.sigev(sigev_notify));
    }
}
