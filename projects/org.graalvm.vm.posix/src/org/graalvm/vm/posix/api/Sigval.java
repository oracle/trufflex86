package org.graalvm.vm.posix.api;

public class Sigval implements Struct {
    public int sival_int = -1;
    public long sival_ptr = -1;

    private long initial_int = -1;

    @Override
    public PosixPointer read32(PosixPointer ptr) {
        sival_int = ptr.getI32();
        sival_ptr = ptr.getI32();
        initial_int = sival_int;
        return ptr.add(4);
    }

    @Override
    public PosixPointer read64(PosixPointer ptr) {
        sival_int = ptr.getI32();
        sival_ptr = ptr.getI64();
        initial_int = sival_int;
        return ptr.add(8);
    }

    @Override
    public PosixPointer write32(PosixPointer ptr) {
        // TODO: check if this really works
        if (sival_int != initial_int) {
            ptr.setI32(sival_int);
        } else {
            ptr.setI32((int) sival_ptr);
        }
        return ptr.add(4);
    }

    @Override
    public PosixPointer write64(PosixPointer ptr) {
        // TODO: check if this really works
        if (sival_int != initial_int) {
            ptr.setI32(sival_int);
        } else {
            ptr.setI64(sival_ptr);
        }
        return ptr.add(8);
    }

    @Override
    public String toString() {
        return String.format("{sival_int=%d, sival_ptr=0x%x}", sival_int, sival_ptr);
    }
}
