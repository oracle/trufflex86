package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class PosixMemory extends Memory {
    private PosixPointer ptr;

    public PosixMemory(PosixPointer ptr, boolean isBE) {
        super(isBE);
        this.ptr = ptr;
    }

    private PosixPointer ptr(long pos) {
        assert pos == (int) pos : String.format("0x%016X vs 0x%016X", pos, (int) pos);
        return ptr.add((int) pos);
    }

    @TruffleBoundary
    @Override
    protected byte i8(long pos) {
        try {
            return ptr(pos).getI8();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected short i16L(long pos) {
        try {
            return Short.reverseBytes(ptr(pos).getI16());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected short i16B(long pos) {
        try {
            return ptr(pos).getI16();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected int i32L(long pos) {
        try {
            return Integer.reverseBytes(ptr(pos).getI32());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected int i32B(long pos) {
        try {
            return ptr(pos).getI32();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected long i64L(long pos) {
        try {
            return Long.reverseBytes(ptr(pos).getI64());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected long i64B(long pos) {
        try {
            return ptr(pos).getI64();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i8(long pos, byte val) {
        try {
            ptr(pos).setI8(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i16L(long pos, short val) {
        try {
            ptr(pos).setI16(Short.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i16B(long pos, short val) {
        try {
            ptr(pos).setI16(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i32L(long pos, int val) {
        try {
            ptr(pos).setI32(Integer.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i32B(long pos, int val) {
        try {
            ptr(pos).setI32(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i64L(long pos, long val) {
        try {
            ptr(pos).setI64(Long.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i64B(long pos, long val) {
        try {
            ptr(pos).setI64(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    public long size() {
        return ptr.size();
    }
}
