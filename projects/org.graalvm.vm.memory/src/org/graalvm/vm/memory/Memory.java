package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.DoubleFreeException;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.posix.api.PosixPointer;

public abstract class Memory {
    private final boolean isBE;
    private boolean free = false;

    protected Memory(boolean isBE) {
        this.isBE = isBE;
    }

    public boolean isBE() {
        return isBE;
    }

    public abstract long size();

    public byte[] getBytes() {
        throw new AssertionError("not implemented");
    }

    protected abstract byte i8(long pos);

    protected abstract short i16B(long pos);

    protected abstract short i16L(long pos);

    protected abstract int i32B(long pos);

    protected abstract int i32L(long pos);

    protected abstract long i64B(long pos);

    protected abstract long i64L(long pos);

    protected abstract void i8(long pos, byte val);

    protected abstract void i16L(long pos, short val);

    protected abstract void i16B(long pos, short val);

    protected abstract void i32L(long pos, int val);

    protected abstract void i32B(long pos, int val);

    protected abstract void i64L(long pos, long val);

    protected abstract void i64B(long pos, long val);

    public byte getI8(long pos) {
        check(pos);
        return i8(pos);
    }

    public short getI16L(long pos) {
        check(pos);
        return i16L(pos);
    }

    public short getI16B(long pos) {
        check(pos);
        return i16B(pos);
    }

    public int getI32L(long pos) {
        check(pos);
        return i32L(pos);
    }

    public int getI32B(long pos) {
        check(pos);
        return i32B(pos);
    }

    public long getI64L(long pos) {
        check(pos);
        return i64L(pos);
    }

    public long getI64B(long pos) {
        check(pos);
        return i64B(pos);
    }

    public Vector128 getI128L(long pos) {
        check(pos);
        long low = i64L(pos);
        long high = i64L(pos + 8);
        return new Vector128(high, low);
    }

    public Vector256 getI256L(long pos) {
        check(pos);
        Vector128 low = getI128L(pos);
        Vector128 high = getI128L(pos + 16);
        return new Vector256(high, low);
    }

    public Vector256 getI256B(long pos) {
        check(pos);
        Vector128 high = getI128B(pos);
        Vector128 low = getI128B(pos + 16);
        return new Vector256(high, low);
    }

    public Vector512 getI512L(long pos) {
        check(pos);
        Vector256 low = getI256L(pos);
        Vector256 high = getI256L(pos + 32);
        return new Vector512(high, low);
    }

    public Vector512 getI512B(long pos) {
        check(pos);
        Vector256 high = getI256B(pos);
        Vector256 low = getI256B(pos + 32);
        return new Vector512(high, low);
    }

    public Vector128 getI128B(long pos) {
        check(pos);
        long high = i64B(pos);
        long low = i64B(pos + 8);
        return new Vector128(high, low);
    }

    public void setI8(long pos, byte val) {
        check(pos);
        i8(pos, val);
    }

    public void setI16L(long pos, short val) {
        check(pos);
        i16L(pos, val);
    }

    public void setI16B(long pos, short val) {
        check(pos);
        i16B(pos, val);
    }

    public void setI32L(long pos, int val) {
        check(pos);
        i32L(pos, val);
    }

    public void setI32B(long pos, int val) {
        check(pos);
        i32B(pos, val);
    }

    public void setI64L(long pos, long val) {
        check(pos);
        i64L(pos, val);
    }

    public void setI64B(long pos, long val) {
        check(pos);
        i64B(pos, val);
    }

    public void setI128L(long pos, Vector128 val) {
        check(pos);
        long high = val.getI64(0);
        long low = val.getI64(1);
        i64L(pos, low);
        i64L(pos + 8, high);
    }

    public void setI128B(long pos, Vector128 val) {
        check(pos);
        long high = val.getI64(0);
        long low = val.getI64(1);
        i64B(pos, high);
        i64B(pos + 8, low);
    }

    public void setI256L(long pos, Vector256 val) {
        check(pos);
        Vector128 high = val.getI128(0);
        Vector128 low = val.getI128(1);
        setI128L(pos, low);
        setI128L(pos + 16, high);
    }

    public void setI256B(long pos, Vector256 val) {
        check(pos);
        Vector128 high = val.getI128(0);
        Vector128 low = val.getI128(1);
        setI128B(pos, high);
        setI128B(pos + 16, low);
    }

    public void setI512L(long pos, Vector512 val) {
        check(pos);
        Vector256 high = val.getI256(0);
        Vector256 low = val.getI256(1);
        setI256L(pos, low);
        setI256L(pos + 32, high);
    }

    public void setI512B(long pos, Vector512 val) {
        check(pos);
        Vector256 high = val.getI256(0);
        Vector256 low = val.getI256(1);
        setI256B(pos, high);
        setI256B(pos + 32, low);
    }

    public short getI16(long pos) {
        if (isBE) {
            return getI16B(pos);
        } else {
            return getI16L(pos);
        }
    }

    public int getI32(long pos) {
        if (isBE) {
            return getI32B(pos);
        } else {
            return getI32L(pos);
        }
    }

    public long getI64(long pos) {
        if (isBE) {
            return getI64B(pos);
        } else {
            return getI64L(pos);
        }
    }

    public Vector128 getI128(long pos) {
        if (isBE) {
            return getI128B(pos);
        } else {
            return getI128L(pos);
        }
    }

    public Vector256 getI256(long pos) {
        if (isBE) {
            return getI256B(pos);
        } else {
            return getI256L(pos);
        }
    }

    public Vector512 getI512(long pos) {
        if (isBE) {
            return getI512B(pos);
        } else {
            return getI512L(pos);
        }
    }

    public void setI16(long pos, short val) {
        if (isBE) {
            setI16B(pos, val);
        } else {
            setI16L(pos, val);
        }
    }

    public void setI32(long pos, int val) {
        if (isBE) {
            setI32B(pos, val);
        } else {
            setI32L(pos, val);
        }
    }

    public void setI64(long pos, long val) {
        if (isBE) {
            setI64B(pos, val);
        } else {
            setI64L(pos, val);
        }
    }

    public void setI128(long pos, Vector128 val) {
        if (isBE) {
            setI128B(pos, val);
        } else {
            setI128L(pos, val);
        }
    }

    public void setI256(long pos, Vector256 val) {
        if (isBE) {
            setI256B(pos, val);
        } else {
            setI256L(pos, val);
        }
    }

    public void setI512(long pos, Vector512 val) {
        if (isBE) {
            setI512B(pos, val);
        } else {
            setI512L(pos, val);
        }
    }

    public void memcpy(byte[] dst, long off) {
        for (int i = 0; i < dst.length; i++) {
            dst[i] = getI8(off + i);
        }
    }

    public byte[] get(long off, long len) {
        assert len == (int) len : String.format("Invalid length 0x%016X", off);
        byte[] out = new byte[(int) len];
        memcpy(out, off);
        return out;
    }

    public void free() {
        if (free) {
            throw new DoubleFreeException(this);
        }
        free = true;
    }

    public boolean isFree() {
        return free;
    }

    protected void check(long pos) {
        if (free) {
            throw new SegmentationViolation(this, pos);
        }
    }

    public PosixPointer getPosixPointer(long off) {
        check(off);
        return null; // TODO
    }
}
