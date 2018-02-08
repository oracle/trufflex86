package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.DoubleFreeException;
import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.everyware.posix.api.PosixPointer;

public abstract class Memory {
    private final boolean isBE;
    private boolean free = false;

    protected Memory(boolean isBE) {
        this.isBE = isBE;
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
