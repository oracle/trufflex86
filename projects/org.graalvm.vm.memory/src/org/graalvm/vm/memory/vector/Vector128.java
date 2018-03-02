package org.graalvm.vm.memory.vector;

import java.util.Arrays;

import com.everyware.util.BitTest;
import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.nodes.ExplodeLoop;

@ValueType
public class Vector128 {
    public static final Vector128 ZERO = new Vector128();

    private final long[] data = new long[2];

    public Vector128() {
    }

    public Vector128(long[] data) {
        assert data.length == 2;
        this.data[0] = data[0];
        this.data[1] = data[1];
    }

    public Vector128(long high, long low) {
        this.data[0] = high;
        this.data[1] = low;
    }

    public double getF64(int i) {
        assert i >= 0 && i < 2;
        return Double.longBitsToDouble(data[i]);
    }

    public void setF64(int i, double val) {
        assert i >= 0 && i < 2;
        data[i] = Double.doubleToLongBits(val);
    }

    public long getI64(int i) {
        assert i >= 0 && i < 2;
        return data[i];
    }

    public void setI64(int i, long val) {
        assert i >= 0 && i < 2;
        data[i] = val;
    }

    public int getI32(int i) {
        assert i >= 0 && i < 4;
        long val = data[i / 2];
        if ((i & 1) == 0) {
            return (int) (val >>> 32);
        } else {
            return (int) val;
        }
    }

    public void setI32(int i, int val) {
        assert i >= 0 && i < 4;
        long old = data[i / 2];
        long mask;
        int shift;
        if ((i & 1) == 0) {
            mask = 0x00000000FFFFFFFFL;
            shift = 32;
        } else {
            mask = 0xFFFFFFFF00000000L;
            shift = 0;
        }
        long result = (old & mask) | (Integer.toUnsignedLong(val) << shift);
        data[i / 2] = result;
    }

    public float getF32(int i) {
        assert i >= 0 && i < 4;
        return Float.intBitsToFloat(getI32(i));
    }

    public void setF32(int i, float val) {
        assert i >= 0 && i < 4;
        setI32(i, Float.floatToRawIntBits(val));
    }

    public short getI16(int i) {
        assert i >= 0 && i < 8;
        long val = data[i / 4];
        int shift = (3 - (i & 3)) << 4;
        return (short) (val >>> shift);
    }

    public void setI16(int i, short val) {
        assert i >= 0 && i < 8;
        long old = data[i / 4];
        int shift = (3 - (i & 3)) << 4;
        long mask = ~(0xFFFFL << shift);
        long result = (old & mask) | ((Short.toUnsignedLong(val) & 0xFFFFL) << shift);
        data[i / 4] = result;
    }

    public byte getI8(int i) {
        assert i >= 0 && i < 16;
        long val = data[i / 8];
        int shift = (7 - (i & 7)) << 3;
        return (byte) (val >>> shift);
    }

    public Vector128 and(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] & x.data[i];
        }
        return new Vector128(result);
    }

    public Vector128 or(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] | x.data[i];
        }
        return new Vector128(result);
    }

    public Vector128 xor(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] ^ x.data[i];
        }
        return new Vector128(result);
    }

    private static long eq(long x, long y, long mask) {
        if ((x & mask) == (y & mask)) {
            return mask;
        } else {
            return 0;
        }
    }

    public Vector128 eq8(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            long r = 0;
            r |= eq(data[i], x.data[i], 0xFF00000000000000L);
            r |= eq(data[i], x.data[i], 0x00FF000000000000L);
            r |= eq(data[i], x.data[i], 0x0000FF0000000000L);
            r |= eq(data[i], x.data[i], 0x000000FF00000000L);
            r |= eq(data[i], x.data[i], 0x00000000FF000000L);
            r |= eq(data[i], x.data[i], 0x0000000000FF0000L);
            r |= eq(data[i], x.data[i], 0x000000000000FF00L);
            r |= eq(data[i], x.data[i], 0x00000000000000FFL);
            result[i] = r;
        }
        return new Vector128(result);
    }

    public Vector128 eq16(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            long r = 0;
            r |= eq(data[i], x.data[i], 0xFFFF000000000000L);
            r |= eq(data[i], x.data[i], 0x0000FFFF00000000L);
            r |= eq(data[i], x.data[i], 0x00000000FFFF0000L);
            r |= eq(data[i], x.data[i], 0x000000000000FFFFL);
            result[i] = r;
        }
        return new Vector128(result);
    }

    public Vector128 eq32(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            long r = 0;
            r |= eq(data[i], x.data[i], 0xFFFFFFFF00000000L);
            r |= eq(data[i], x.data[i], 0x00000000FFFFFFFFL);
            result[i] = r;
        }
        return new Vector128(result);
    }

    @ExplodeLoop
    public long byteMaskMSB() {
        long result = 0;
        long o = 1L << (data.length * 8 - 1);
        for (int i = 0; i < data.length; i++) {
            assert o != 0;
            long val = data[i];
            long mask = 0x8000000000000000L;
            for (int n = 0; n < 8; n++) {
                if (BitTest.test(val, mask)) {
                    result |= o;
                }
                o >>>= 1;
                mask >>>= 8;
            }
            assert mask == 0;
        }
        assert o == 0;
        return result;
    }

    @ExplodeLoop
    public Vector128 shl(int n) {
        assert n > 0 && n < 128;
        if (n < 64) {
            long overflow = 0;
            long overflowShift = 64 - n;
            long overflowMask = 0;
            for (int i = 0, bit = 0; i < 64; i++, bit <<= 1) {
                if (i < n) {
                    overflowMask |= bit;
                }
            }
            long[] result = new long[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = overflow | (data[i] << n);
                overflow = (data[i] >> overflowShift) & overflowMask;
            }
            return new Vector128(result);
        } else {
            throw new AssertionError("not yet implemented");
        }
    }

    @Override
    public int hashCode() {
        long result = 0;
        for (int i = 0; i < data.length; i++) {
            result ^= data[i];
        }
        return (int) result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Vector128)) {
            return false;
        }
        Vector128 v = (Vector128) o;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != v.data[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Vector128 clone() {
        long[] value = Arrays.copyOf(data, data.length);
        return new Vector128(value);
    }

    @Override
    public String toString() {
        return String.format("0x%016x%016x", data[0], data[1]);
    }
}
