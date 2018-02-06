package org.graalvm.vm.x86.isa;

import com.everyware.util.BitTest;

// manual p69
public class AMD64RexPrefix {
    private final byte prefix;
    public final boolean w;
    public final boolean r;
    public final boolean x;
    public final boolean b;

    public AMD64RexPrefix(byte prefix) {
        this.prefix = prefix;
        if ((prefix & 0xF0) != 0x40) {
            throw new IllegalArgumentException(String.format("not a REX prefix: %02x", Byte.toUnsignedInt(prefix)));
        }
        w = BitTest.test(prefix, 1 << 3);
        r = BitTest.test(prefix, 1 << 2);
        x = BitTest.test(prefix, 1 << 1);
        b = BitTest.test(prefix, 1 << 0);
    }

    public byte getPrefix() {
        return prefix;
    }

    public static boolean isREX(byte op) {
        return (op & 0xF0) == 0x40;
    }

    @Override
    public String toString() {
        return String.format("REX[w=%d,r=%d,x=%d,b=%d]", w ? 1 : 0, r ? 1 : 0, x ? 1 : 0, b ? 1 : 0);
    }
}
