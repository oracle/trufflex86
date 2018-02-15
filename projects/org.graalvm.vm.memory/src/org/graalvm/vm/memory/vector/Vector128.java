package org.graalvm.vm.memory.vector;

import com.oracle.truffle.api.CompilerDirectives.ValueType;

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

    public Vector128(long low, long high) {
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

    public Vector128 xor(Vector128 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] ^ x.data[i];
        }
        return new Vector128(result);
    }

    @Override
    public String toString() {
        return String.format("0x%016x%016x", data[0], data[1]);
    }
}
