package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.everyware.posix.api.MemoryFaultException;
import com.everyware.posix.api.PosixPointer;

public class PosixVirtualMemoryPointer implements PosixPointer {
    private final VirtualMemory memory;
    private final long offset;

    public PosixVirtualMemoryPointer(VirtualMemory memory, long offset) {
        this.memory = memory;
        this.offset = offset;
    }

    @Override
    public long getAddress() {
        return offset;
    }

    @Override
    public PosixPointer add(int off) {
        return new PosixVirtualMemoryPointer(memory, offset + off);
    }

    @Override
    public byte getI8() throws MemoryFaultException {
        try {
            return memory.getI8(offset);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public short getI16() throws MemoryFaultException {
        try {
            return memory.getI16(offset);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public int getI32() throws MemoryFaultException {
        try {
            return memory.getI32(offset);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public long getI64() throws MemoryFaultException {
        try {
            return memory.getI64(offset);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI8(byte val) throws MemoryFaultException {
        try {
            memory.setI8(offset, val);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI16(short val) throws MemoryFaultException {
        try {
            memory.setI16(offset, val);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI32(int val) throws MemoryFaultException {
        try {
            memory.setI32(offset, val);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI64(long val) throws MemoryFaultException {
        try {
            memory.setI64(offset, val);
        } catch (SegmentationViolation e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("PosixVirtualMemoryPointer[0x%016X]", offset);
    }
}
