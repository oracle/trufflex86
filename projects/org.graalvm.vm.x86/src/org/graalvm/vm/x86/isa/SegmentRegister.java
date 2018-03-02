package org.graalvm.vm.x86.isa;

public enum SegmentRegister {
    CS,
    DS,
    ES,
    FS,
    GS,
    SS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
