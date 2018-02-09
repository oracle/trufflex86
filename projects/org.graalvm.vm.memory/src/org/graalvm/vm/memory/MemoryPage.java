package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.SegmentationViolation;

public class MemoryPage {
    public final Memory memory;
    public final long base;
    public final long size;
    public final long end;
    private final long offset;

    public boolean r;
    public boolean w;
    public boolean x;

    public final String name;
    public final long fileOffset;

    public MemoryPage(Memory memory, long base, long size) {
        this.memory = memory;
        this.base = base;
        this.size = size;
        this.end = base + size;
        offset = 0;
        r = true;
        w = true;
        x = true;
        name = null;
        fileOffset = 0;
    }

    public MemoryPage(Memory memory, long base, long size, String name) {
        this.memory = memory;
        this.base = base;
        this.size = size;
        this.end = base + size;
        this.name = name;
        offset = 0;
        r = true;
        w = true;
        x = true;
        fileOffset = 0;
    }

    protected MemoryPage(MemoryPage page) {
        this.memory = page.memory;
        this.base = page.base;
        this.size = page.size;
        this.end = page.end;
        this.offset = page.offset;
        this.r = page.r;
        this.w = page.w;
        this.x = page.x;
        this.name = page.name;
        this.fileOffset = page.fileOffset;
    }

    public MemoryPage(MemoryPage page, long address, long size) {
        this.memory = page.memory;
        this.base = address;
        this.size = size;
        this.end = base + size;
        this.r = page.r;
        this.w = page.w;
        this.x = page.x;
        this.offset = address - page.base;
        this.name = page.name;
        this.fileOffset = page.fileOffset + (address - page.base);
    }

    public boolean contains(long address) {
        return Long.compareUnsigned(address, base) >= 0 && Long.compareUnsigned(address, end) < 0;
    }

    public Memory getMemory() {
        return memory;
    }

    public long getOffset(long addr) {
        return addr - base + offset;
    }

    public long getBase() {
        return base;
    }

    public long getEnd() {
        return end;
    }

    public byte getI8(long addr) {
        if (!r) {
            throw new SegmentationViolation(this, addr);
        }
        try {
            return memory.getI8(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public short getI16(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI16(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public int getI32(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI32(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public long getI64(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI64(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI8(long addr, byte val) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI8(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI16(long addr, short val) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI16(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI32(long addr, int val) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI32(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI64(long addr, long val) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI64(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public byte[] get(long addr, long len) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.get(getOffset(addr), len);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    @Override
    public String toString() {
        return String.format("%016x-%016x %c%c%cp %08x 00:00 0 %s",
                        base, end, r ? 'r' : '-', w ? 'w' : '-', x ? 'x' : '-', fileOffset,
                        name != null ? name : "");
    }
}
