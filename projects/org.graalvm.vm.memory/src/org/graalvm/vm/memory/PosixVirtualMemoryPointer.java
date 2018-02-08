package org.graalvm.vm.memory;

import com.everyware.posix.api.PosixPointer;

public class PosixVirtualMemoryPointer implements PosixPointer {
    private final VirtualMemory memory;
    private final long offset;

    public PosixVirtualMemoryPointer(VirtualMemory memory, long offset) {
        this.memory = memory;
        this.offset = offset;
    }

    @Override
    public PosixPointer add(int off) {
        return new PosixVirtualMemoryPointer(memory, offset + off);
    }

    @Override
    public byte getI8() {
        return memory.getI8(offset);
    }

    @Override
    public short getI16() {
        return memory.getI16(offset);
    }

    @Override
    public int getI32() {
        return memory.getI32(offset);
    }

    @Override
    public long getI64() {
        return memory.getI64(offset);
    }

    @Override
    public void setI8(byte val) {
        memory.setI8(offset, val);
    }

    @Override
    public void setI16(short val) {
        memory.setI16(offset, val);
    }

    @Override
    public void setI32(int val) {
        memory.setI32(offset, val);
    }

    @Override
    public void setI64(long val) {
        memory.setI64(offset, val);
    }

    @Override
    public String toString() {
        return String.format("PosixVirtualMemoryPointer[0x%016X]", offset);
    }
}
