package org.graalvm.vm.memory;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixException;
import com.everyware.posix.api.PosixPointer;
import com.everyware.util.io.Endianess;
import com.oracle.truffle.api.CompilerDirectives;

public class VirtualMemory {
    private static final boolean DEBUG = false;

    public static final long PAGE_SIZE = 4096;
    public static final long PAGE_MASK = ~(PAGE_SIZE - 1);

    // public static final long POINTER_BASE = 0x0800000000000000L;
    // public static final long POINTER_END = 0x8000000000000000L;
    public static final long POINTER_BASE = 0x00007f0000000000L;
    public static final long POINTER_END = 0x00007fff00000000L;

    private final NavigableMap<Long, MemoryPage> pages;

    private MemoryAllocator allocator;

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
        allocator = new MemoryAllocator(POINTER_BASE, POINTER_END - POINTER_BASE);
        brk = 0;
        reportedBrk = brk;
        debugMemory = DEBUG;
        cache = null;
        cache2 = null;
        cacheHits = 0;
        cacheMisses = 0;
        set64bit();
    }

    public void set32bit() {
        mask = 0x00000000FFFFFFFFL;
    }

    public void set64bit() {
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

    public long brk(long addr) {
        if (Long.compareUnsigned(addr, brk) > 0 && Long.compareUnsigned(addr, POINTER_BASE) <= 0) {
            long sz = addr - brk;
            Memory mem = new ByteMemory(sz, false);
            MemoryPage page = new MemoryPage(mem, brk, sz, "[heap]");
            add(page);
            brk = addr;
            reportedBrk = brk;
            return brk;
        } else {
            reportedBrk = addr;
            return reportedBrk;
        }
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
        boolean ok = Long.compareUnsigned(page.end, POINTER_BASE) <= 0 || Long.compareUnsigned(page.end, POINTER_END) > 0;
        if (!ok) {
            allocator.allocat(page.base, page.size);
        }
        try {
            MemoryPage oldPage = get(page.base);
            if (page.contains(oldPage.base) && page.contains(oldPage.end - 1)) {
                pages.remove(oldPage.base);
            } else {
                if (DEBUG) {
                    CompilerDirectives.transferToInterpreter();
                    System.out.printf("Splitting old page: 0x%016X-0x%016X, new page is 0x%016X-0x%016X\n", oldPage.base, oldPage.end, page.base, page.end);
                }
                long size1 = page.base - oldPage.base;
                long size2 = oldPage.end - page.end;
                if (DEBUG) {
                    CompilerDirectives.transferToInterpreter();
                    System.out.printf("size1 = 0x%016X, size2 = 0x%016X\n", size1, size2);
                }
                if (size1 > 0) {
                    MemoryPage p = new MemoryPage(oldPage, oldPage.base, size1);
                    pages.put(oldPage.base, p);
                    cache = null;
                    cache2 = null;
                    if (DEBUG) {
                        CompilerDirectives.transferToInterpreter();
                        System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X]\n", oldPage.base, pages.get(oldPage.base).base, pages.get(oldPage.base).end);
                    }
                }
                if (size2 > 0) {
                    MemoryPage p = new MemoryPage(oldPage, page.end, size2);
                    pages.put(page.end, p);
                    cache = null;
                    cache2 = null;
                    if (DEBUG) {
                        CompilerDirectives.transferToInterpreter();
                        System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X]\n", page.end, pages.get(page.end).base, pages.get(page.end).end);
                    }
                }
            }
        } catch (SegmentationViolation e) {
        }
        pages.put(page.base, page);
        cache = null;
        cache2 = null;
        if (page.base != pageStart(page.base)) {
            if (DEBUG) {
                System.out.printf("bad page start: 0x%016X, should be 0x%016X\n", page.base,
                                pageStart(page.base));
            }
            long base = pageStart(page.base);
            long size = page.base - base;
            try {
                get(base);
            } catch (SegmentationViolation e) {
                Memory buf = new ByteMemory(size);
                MemoryPage bufpage = new MemoryPage(buf, base, size, page.name);
                pages.put(base, bufpage);
                cache = null;
                cache2 = null;
            }
        }
        if (DEBUG) {
            printLayout();
        }
    }

    public void remove(long addr, long length) throws PosixException {
        cache = null;
        cache2 = null;
        long address = addr(addr);
        if ((address & ~PAGE_MASK) != 0) {
            throw new PosixException(Errno.EINVAL);
        }
        try {
            for (long p = address; Long.compareUnsigned(p, address + length) < 0;) {
                MemoryPage page = get(p);
                if (p != page.base) {
                    // TODO: split
                    throw new AssertionError("split not yet implemented");
                }
                pages.remove(page.base);
                allocator.free(page.base, page.size);
                p = page.end;
            }
        } catch (SegmentationViolation e) {
            // swallow
        }
    }

    public MemoryPage allocate(long size) {
        long base = allocator.alloc(size);
        if (base == 0) {
            return null;
        } else {
            Memory mem = new ByteMemory(size);
            MemoryPage page = new MemoryPage(mem, base, size);
            add(page);
            return page;
        }
    }

    public MemoryPage allocate(Memory memory, long size, String name) {
        long base = allocator.alloc(size);
        if (base == 0) {
            return null;
        } else {
            MemoryPage page = new MemoryPage(memory, base, size, name);
            add(page);
            return page;
        }
    }

    public void free(long address) {
        MemoryPage page = pages.remove(address);
        allocator.free(address, page.size);
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
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                byte[] bytes = new byte[]{getI8(ptr), getI8(ptr + 1)};
                boolean isBE = page.getMemory().isBE();
                short value = isBE ? Endianess.get16bitBE(bytes) : Endianess.get16bitLE(bytes);
                logMemoryRead(address, 2, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 2);
                throw t;
            }
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
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                byte[] bytes = new byte[]{getI8(ptr), getI8(ptr + 1), getI8(ptr + 2), getI8(ptr + 3)};
                boolean isBE = page.getMemory().isBE();
                int value = isBE ? Endianess.get32bitBE(bytes) : Endianess.get32bitLE(bytes);
                logMemoryRead(address, 4, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 4);
                throw t;
            }
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
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                byte[] bytes = new byte[]{getI8(ptr), getI8(ptr + 1), getI8(ptr + 2), getI8(ptr + 3), getI8(ptr + 4), getI8(ptr + 5), getI8(ptr + 6), getI8(ptr + 7)};
                boolean isBE = page.getMemory().isBE();
                long value = isBE ? Endianess.get64bitBE(bytes) : Endianess.get64bitLE(bytes);
                logMemoryRead(address, 8, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 8);
                throw t;
            }
        } catch (Throwable t) {
            logMemoryRead(address, 8);
            throw t;
        }
    }

    public Vector128 getI128(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            Vector128 v = page.getI128(ptr);
            logMemoryRead(address, v);
            return v;
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                boolean isBE = page.getMemory().isBE();
                Vector128 value = isBE ? new Vector128(getI64(ptr), getI64(ptr + 8)) : new Vector128(getI64(ptr + 8), getI64(ptr));
                logMemoryRead(address, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 16);
                throw t;
            }
        } catch (Throwable t) {
            logMemoryRead(address, 16);
            throw t;
        }
    }

    public Vector256 getI256(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            Vector256 v = page.getI256(ptr);
            logMemoryRead(address, v);
            return v;
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                boolean isBE = page.getMemory().isBE();
                Vector256 value = isBE ? new Vector256(getI128(ptr), getI128(ptr + 16)) : new Vector256(getI128(ptr + 16), getI128(ptr));
                logMemoryRead(address, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 32);
                throw t;
            }
        } catch (Throwable t) {
            logMemoryRead(address, 32);
            throw t;
        }
    }

    public Vector512 getI512(long address) {
        long ptr = addr(address);
        try {
            MemoryPage page = get(ptr);
            Vector512 v = page.getI512(ptr);
            logMemoryRead(address, v);
            return v;
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            try {
                MemoryPage page = get(ptr);
                boolean isBE = page.getMemory().isBE();
                Vector512 value = isBE ? new Vector512(getI256(ptr), getI256(ptr + 32)) : new Vector512(getI256(ptr + 32), getI256(ptr));
                logMemoryRead(address, value);
                return value;
            } catch (Throwable t) {
                logMemoryRead(address, 64);
                throw t;
            }
        } catch (Throwable t) {
            logMemoryRead(address, 64);
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
        try {
            page.setI16(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            byte[] bytes = new byte[2];
            if (isBE) {
                Endianess.set16bitBE(bytes, 0, val);
            } else {
                Endianess.set16bitLE(bytes, 0, val);
            }
            setI8(address, bytes[0]);
            setI8(address + 1, bytes[1]);
        }
    }

    public void setI32(long address, int val) {
        logMemoryWrite(address, 4, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        try {
            page.setI32(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            byte[] bytes = new byte[4];
            if (isBE) {
                Endianess.set32bitBE(bytes, 0, val);
            } else {
                Endianess.set32bitLE(bytes, 0, val);
            }
            for (int i = 0; i < bytes.length; i++) {
                setI8(address + i, bytes[i]);
            }
        }
    }

    public void setI64(long address, long val) {
        logMemoryWrite(address, 8, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        try {
            page.setI64(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            byte[] bytes = new byte[8];
            if (isBE) {
                Endianess.set64bitBE(bytes, 0, val);
            } else {
                Endianess.set64bitLE(bytes, 0, val);
            }
            for (int i = 0; i < bytes.length; i++) {
                setI8(address + i, bytes[i]);
            }
        }
    }

    public void setI128(long address, Vector128 val) {
        logMemoryWrite(address, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        try {
            page.setI128(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            if (isBE) {
                setI64(address, val.getI64(0));
                setI64(address + 8, val.getI64(1));
            } else {
                setI64(address, val.getI64(1));
                setI64(address + 8, val.getI64(0));
            }
        }
    }

    public void setI256(long address, Vector256 val) {
        logMemoryWrite(address, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        try {
            page.setI256(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            if (isBE) {
                setI128(address, val.getI128(0));
                setI128(address + 16, val.getI128(1));
            } else {
                setI128(address, val.getI128(1));
                setI128(address + 16, val.getI128(0));
            }
        }
    }

    public void setI512(long address, Vector512 val) {
        logMemoryWrite(address, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        try {
            page.setI512(ptr, val);
        } catch (SegmentationViolation e) { // unaligned access across page boundary
            boolean isBE = page.getMemory().isBE();
            if (isBE) {
                setI256(address, val.getI256(0));
                setI256(address + 16, val.getI256(1));
            } else {
                setI256(address, val.getI256(1));
                setI256(address + 16, val.getI256(0));
            }
        }
    }

    public void dump(long p, int size) {
        // disable memory access log during dump
        boolean wasDebug = debugMemory;
        debugMemory = false;
        try {
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
                        if (!isPrintable(u8)) {
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
        } finally {
            debugMemory = wasDebug;
        }
    }

    private void logMemoryRead(long address, int size) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read %d bytes\n", addr(addr), size);
        }
    }

    private void logMemoryRead(long address, int size, long value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryRead(long address, int size, int value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%08x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryRead(long address, int size, short value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%04x)\n", addr(addr), size, value);
        }
    }

    private static boolean isPrintable(byte value) {
        return value >= 0x20 && value <= 0x7e; // ascii
    }

    private void logMemoryRead(long address, int size, byte value) {
        if (debugMemory) {
            long addr = addr(address);
            if (isPrintable(value)) {
                System.out.printf("Memory access to 0x%016x: read %d byte(s) (0x%02x, '%c')\n", addr(addr), size, value, new Character((char) (value & 0x7F)));
            } else {
                System.out.printf("Memory access to 0x%016x: read %d byte(s) (0x%02x)\n", addr(addr), size, value);
            }
        }
    }

    private void logMemoryRead(long address, Vector128 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 16 bytes (%s)\n", addr(addr), value);
        }
    }

    private void logMemoryRead(long address, Vector256 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 32 bytes (%s)\n", addr(addr), value);
        }
    }

    private void logMemoryRead(long address, Vector512 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 64 bytes (%s)\n", addr(addr), value);
        }
    }

    private void logMemoryWrite(long address, int size, long value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryWrite(long address, int size, int value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%08x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryWrite(long address, int size, short value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%04x)\n", addr(addr), size, value);
        }
    }

    private void logMemoryWrite(long address, int size, byte value) {
        if (debugMemory) {
            long addr = addr(address);
            if (isPrintable(value)) {
                System.out.printf("Memory access to 0x%016x: write %d byte(s) (0x%02x, '%c')\n", addr(addr), size, value, new Character((char) (value & 0x7F)));
            } else {
                System.out.printf("Memory access to 0x%016x: write %d byte(s) (0x%02x)\n", addr(addr), size, value);
            }
        }
    }

    private void logMemoryWrite(long address, Vector128 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 16 bytes (%s)\n", addr(addr), value);
        }
    }

    private void logMemoryWrite(long address, Vector256 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 32 bytes (%s)\n", addr(addr), value);
        }
    }

    private void logMemoryWrite(long address, Vector512 value) {
        if (debugMemory) {
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 64 bytes (%s)\n", addr(addr), value);
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
