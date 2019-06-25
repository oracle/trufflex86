package org.graalvm.vm.posix.api;

public class Itimerspec implements Struct {
    public final Timespec it_interval = new Timespec();
    public final Timespec it_value = new Timespec();

    @Override
    public PosixPointer read32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = it_interval.read32(p);
        p = it_value.read32(p);
        return p;
    }

    @Override
    public PosixPointer read64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = it_interval.read64(p);
        p = it_value.read64(p);
        return p;
    }

    @Override
    public PosixPointer write32(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = it_interval.write32(p);
        p = it_value.write32(p);
        return p;
    }

    @Override
    public PosixPointer write64(PosixPointer ptr) {
        PosixPointer p = ptr;
        p = it_interval.write64(p);
        p = it_value.write64(p);
        return p;
    }

    @Override
    public String toString() {
        return String.format("{it_interval=%s, it_value=%s}", it_interval, it_value);
    }
}
