package org.graalvm.vm.x86.isa;

public abstract class CodeReader {
    public abstract byte read8();

    public abstract boolean isAvailable();

    public short read16() {
        byte a = read8();
        byte b = read8();
        return (short) (Byte.toUnsignedInt(a) | (Byte.toUnsignedInt(b) << 8));
    }

    public int read32() {
        byte a = read8();
        byte b = read8();
        byte c = read8();
        byte d = read8();
        return Byte.toUnsignedInt(a) | (Byte.toUnsignedInt(b) << 8) | (Byte.toUnsignedInt(c) << 16) | (Byte.toUnsignedInt(d) << 24);
    }
}
