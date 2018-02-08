package org.graalvm.vm.memory;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.graalvm.vm.memory.exception.SegmentationViolation;

import com.everyware.posix.api.PosixPointer;

public class VirtualMemory {
    public static final long PAGE_SIZE = 4096;
    public static final long PAGE_MASK = ~(PAGE_SIZE - 1);

    private final NavigableMap<Long, MemoryPage> pages;

    // private MemoryAllocator allocator;

    private long mask;

    private long brk;
    private long reportedBrk;

    private boolean debugMemory;

    private MemoryPage cache;
    private MemoryPage cache2;
    private long cacheHits;
    private long cacheMisses;

    public VirtualMemory() {
        pages = new TreeMap<>(Long::compareUnsigned);
        // allocator = new MemoryAllocator(POINTER_BASE, POINTER_END - POINTER_BASE);
        brk = 0;
        reportedBrk = brk;
        debugMemory = false;
        cache = null;
        cache2 = null;
        cacheHits = 0;
        cacheMisses = 0;
        mask = 0xFFFFFFFFFFFFFFFFL;
    }

    public long addr(long addr) {
        return addr & mask;
    }

    public void setBrk(long brk) {
        this.brk = brk;
        this.reportedBrk = brk;
    }

    public long brk() {
        return reportedBrk;
    }

    public Collection<MemoryPage> getPages() {
        return Collections.unmodifiableCollection(pages.values());
    }

    public long pageStart(long addr) {
        return addr(addr) & PAGE_MASK;
    }

    public long roundToPageSize(long size) {
        long base = size & PAGE_MASK;
        if (base != size) {
            return base + PAGE_SIZE;
        } else {
            return base;
        }
    }

    public void add(MemoryPage page) {
        pages.put(page.base, page);
        cache = null;
        cache2 = null;
    }

    public void printAccessError(long addr, MemoryPage page) {
        if (page != null) {
            System.err.printf("Tried to access 0x%016X, nearest page is P[0x%016X;0x%016X]\n", addr, page.base, page.end);
        } else {
            System.err.printf("Tried to access 0x%016X\n", addr);
        }
        printLayout(System.err);
    }

    public MemoryPage get(long address) {
        long addr = addr(address);
        // TODO: fix this!
        if (cache != null && cache.contains(addr)) {
            cacheHits++;
            return cache;
        } else if (cache2 != null && cache2.contains(addr)) {
            cacheHits++;
            // swap cache entries
            MemoryPage page = cache2;
            cache2 = cache;
            cache = page;
            return page;
        } else {
            cacheMisses++;
        }
        Map.Entry<Long, MemoryPage> entry = pages.floorEntry(addr);
        if (entry == null) {
            throw new SegmentationViolation(addr);
        }
        MemoryPage page = entry.getValue();
        if (page.contains(addr)) {
            if (cache != null) {
                cache2 = page;
            } else {
                cache = page;
            }
            return page;
        } else {
            throw new SegmentationViolation(addr);
        }
    }

    public PosixPointer getPosixPointer(long address) {
        long addr = addr(address);
        return new PosixVirtualMemoryPointer(this, addr);
    }

    public boolean contains(long address) {
        long addr = addr(address);
        Map.Entry<Long, MemoryPage> entry = pages.floorEntry(addr);
        if (entry == null) {
            return false;
        }
        MemoryPage page = entry.getValue();
        return page.contains(addr);
    }

    public byte getI8(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            byte val = page.getI8(ptr);
            logMemoryRead(address, 1, val);
            return val;
        } catch (Throwable t) {
            logMemoryRead(address, 1);
            throw t;
        }
    }

    public short getI16(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            short val = page.getI16(ptr);
            logMemoryRead(address, 2, val);
            return val;
        } catch (Throwable t) {
            logMemoryRead(address, 2);
            throw t;
        }
    }

    public int getI32(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            int v = page.getI32(ptr);
            logMemoryRead(address, 4, v);
            return v;
        } catch (Throwable t) {
            logMemoryRead(address, 4);
            throw t;
        }
    }

    public long getI64(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            long v = page.getI64(ptr);
            logMemoryRead(address, 8, v);
            return v;
        } catch (Throwable t) {
            logMemoryRead(address, 8);
            throw t;
        }
    }

    public void setI8(long address, byte val) {
        logMemoryWrite(address, 1, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        page.setI8(ptr, val);
    }

    public void setI16(long address, short val) {
        logMemoryWrite(address, 2, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        page.setI16(ptr, val);
    }

    public void setI32(long address, int val) {
        logMemoryWrite(address, 4, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        page.setI32(ptr, val);
    }

    public void setI64(long address, long val) {
        logMemoryWrite(address, 8, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        page.setI64(ptr, val);
    }

    public void dump(long p, int size) {
        long ptr = p;
        System.out.printf("memory at 0x%016x:\n", ptr);
        long ptr2 = ptr;
        boolean nl = true;
        for (int i = 0; i < size; i++) {
            nl = true;
            if (i % 16 == 0) {
                System.out.printf("%016x:", ptr);
            }
            byte u8 = getI8(ptr);
            ptr++;
            System.out.printf(" %02x", Byte.toUnsignedInt(u8));
            if (i % 16 == 15) {
                System.out.print("   ");
                for (int j = 0; j < 16; j++) {
                    u8 = getI8(ptr2);
                    ptr2++;
                    char ch = (char) (u8 & 0xff);
                    if (ch < 32 || ch > 127) {
                        ch = '.';
                    }
                    System.out.printf("%c", ch);
                }
                System.out.println();
                nl = false;
            }
        }
        if (nl) {
            System.out.println();
        }
    }

    private void logMemoryRead(long address, int size) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016X: read %d bytes\n", addr(addr), size);
        }
    }

    private void logMemoryRead(long address, int size, long value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016X: read %d bytes (0x%016x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryWrite(long address, int size, long value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016X: write %d bytes (0x%016X)\n", addr(addr), size, value);
        }
    }

    public void printLayout() {
        printLayout(System.out);
    }

    public void printLayout(PrintStream out) {
        out.println("Memory map:");
        pages.entrySet().stream().map((x) -> x.getValue().toString()).forEachOrdered(out::println);
    }

    public void printStats(PrintStream out) {
        out.printf("Cache: %d hits, %d misses (%5.3f%% hits)\n", cacheHits, cacheMisses,
                        (double) cacheHits / (double) (cacheHits + cacheMisses));
    }
}
