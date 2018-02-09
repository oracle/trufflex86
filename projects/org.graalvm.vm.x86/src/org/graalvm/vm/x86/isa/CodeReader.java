package org.graalvm.vm.x86.isa;

public abstract class CodeReader {
    public abstract byte read8();

    public boolean isAvailable() {
        return false;
    }

    public abstract long getPC();

    public void setPC(@SuppressWarnings("unused") long pc) {
        throw new AssertionError("not implemented");
    }

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

    public long read64() {
        byte a = read8();
        byte b = read8();
        byte c = read8();
        byte d = read8();
        byte e = read8();
        byte f = read8();
        byte g = read8();
        byte h = read8();
        return Byte.toUnsignedLong(a) | (Byte.toUnsignedLong(b) << 8) | (Byte.toUnsignedLong(c) << 16) | (Byte.toUnsignedLong(d) << 24) | (Byte.toUnsignedLong(e) << 32) |
                        (Byte.toUnsignedLong(f) << 40) | (Byte.toUnsignedLong(g) << 48) | (Byte.toUnsignedLong(h) << 56);
    }
}
