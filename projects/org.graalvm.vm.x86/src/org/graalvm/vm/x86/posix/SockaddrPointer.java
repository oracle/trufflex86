package org.graalvm.vm.x86.posix;

import com.everyware.posix.api.PosixPointer;

public class SockaddrPointer implements PosixPointer {
    private PosixPointer ptr;
    private int offset;

    public SockaddrPointer(PosixPointer ptr) {
        this.ptr = ptr;
        this.offset = 0;
    }

    private SockaddrPointer(SockaddrPointer p, int off) {
        this.ptr = p.ptr.add(off);
        this.offset = p.offset + off;
    }

    @Override
    public PosixPointer add(int off) {
        return new SockaddrPointer(this, off);
    }

    @Override
    public byte getI8() {
        return ptr.getI8();
    }

    @Override
    public short getI16() {
        if (offset >= 2) {
            return Short.reverseBytes(ptr.getI16());
        } else {
            return ptr.getI16();
        }
    }

    @Override
    public int getI32() {
        if (offset >= 2) {
            return Integer.reverseBytes(ptr.getI32());
        } else {
            return ptr.getI32();
        }
    }

    @Override
    public long getI64() {
        if (offset >= 2) {
            return Long.reverseBytes(ptr.getI64());
        } else {
            return ptr.getI64();
        }
    }

    @Override
    public void setI8(byte val) {
        ptr.setI8(val);
    }

    @Override
    public void setI16(short val) {
        if (offset >= 2) {
            ptr.setI16(Short.reverseBytes(val));
        } else {
            ptr.setI16(val);
        }
    }

    @Override
    public void setI32(int val) {
        if (offset >= 2) {
            ptr.setI32(Integer.reverseBytes(val));
        } else {
            ptr.setI32(val);
        }
    }

    @Override
    public void setI64(long val) {
        if (offset >= 2) {
            ptr.setI64(Long.reverseBytes(val));
        } else {
            ptr.setI64(val);
        }
    }
}
