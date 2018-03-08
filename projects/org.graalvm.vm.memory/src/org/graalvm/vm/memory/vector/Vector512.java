package org.graalvm.vm.memory.vector;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.ValueType;
import com.oracle.truffle.api.nodes.ExplodeLoop;

@ValueType
public class Vector512 implements Cloneable {
    public static final Vector512 ZERO = new Vector512();

    private final long[] data = new long[8];

    public Vector512() {
    }

    public Vector512(long[] data) {
        assert data.length == 8;
        for (int i = 0; i < 8; i++) {
            this.data[i] = data[i];
        }
    }

    public Vector512(Vector256 high, Vector256 low) {
        this.data[0] = high.getI64(0);
        this.data[1] = high.getI64(1);
        this.data[2] = high.getI64(2);
        this.data[3] = high.getI64(3);
        this.data[4] = low.getI64(0);
        this.data[5] = low.getI64(1);
        this.data[6] = low.getI64(2);
        this.data[7] = low.getI64(3);
    }

    @ExplodeLoop
    public Vector256 getI256(int i) {
        assert i >= 0 && i < 2;
        long[] val = new long[4];
        for (int j = 0; j < val.length; j++) {
            val[j] = data[2 * i + j];
        }
        return new Vector256(val);
    }

    @ExplodeLoop
    public void setI256(int i, Vector256 val) {
        assert i >= 0 && i < 2;
        for (int j = 0; j < 4; j++) {
            data[2 * i + j] = val.getI64(j);
        }
    }

    public Vector128 getI128(int i) {
        assert i >= 0 && i < 4;
        long[] val = new long[2];
        val[0] = data[2 * i + 0];
        val[1] = data[2 * i + 1];
        return new Vector128(val);
    }

    public void setI128(int i, Vector128 val) {
        assert i >= 0 && i < 4;
        data[2 * i + 0] = val.getI64(0);
        data[2 * i + 1] = val.getI64(1);
    }

    public double getF64(int i) {
        assert i >= 0 && i < 8;
        return Double.longBitsToDouble(data[i]);
    }

    public void setF64(int i, double val) {
        assert i >= 0 && i < 8;
        data[i] = Double.doubleToLongBits(val);
    }

    public long getI64(int i) {
        assert i >= 0 && i < 8;
        return data[i];
    }

    public void setI64(int i, long val) {
        assert i >= 0 && i < 8;
        data[i] = val;
    }

    public int getI32(int i) {
        assert i >= 0 && i < 16;
        long val = data[i / 2];
        if ((i & 1) == 0) {
            return (int) (val >>> 32);
        } else {
            return (int) val;
        }
    }

    public void setI32(int i, int val) {
        assert i >= 0 && i < 16;
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
        assert i >= 0 && i < 16;
        return Float.intBitsToFloat(getI32(i));
    }

    public void setF32(int i, float val) {
        assert i >= 0 && i < 16;
        setI32(i, Float.floatToRawIntBits(val));
    }

    public short getI16(int i) {
        assert i >= 0 && i < 32;
        long val = data[i / 4];
        int shift = (3 - (i & 3)) << 4;
        return (short) (val >>> shift);
    }

    public void setI16(int i, short val) {
        assert i >= 0 && i < 32;
        long old = data[i / 4];
        int shift = (3 - (i & 3)) << 4;
        long mask = ~(0xFFFFL << shift);
        long result = (old & mask) | ((Short.toUnsignedLong(val) & 0xFFFFL) << shift);
        data[i / 4] = result;
    }

    public byte getI8(int i) {
        assert i >= 0 && i < 64;
        long val = data[i / 8];
        int shift = (7 - (i & 7)) << 3;
        return (byte) (val >>> shift);
    }

    public Vector512 xor(Vector512 x) {
        long[] result = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] ^ x.data[i];
        }
        return new Vector512(result);
    }

    @Override
    public Vector512 clone() {
        long[] value = Arrays.copyOf(data, data.length);
        return new Vector512(value);
    }

    @Override
    public String toString() {
        return String.format("0x%016x%016x%016x%016x%016x%016x%016x%016x", data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
    }
}
