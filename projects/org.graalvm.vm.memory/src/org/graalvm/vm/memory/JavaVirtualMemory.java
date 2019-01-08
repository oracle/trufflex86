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
import com.everyware.util.io.Endianess;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class JavaVirtualMemory extends VirtualMemory {
    private final NavigableMap<Long, MemoryPage> pages;

    private MemoryPage cache;
    private MemoryPage cache2;
    private long cacheHits;
    private long cacheMisses;

    public JavaVirtualMemory() {
        this(POINTER_BASE, POINTER_END);
    }

    public JavaVirtualMemory(long lo, long hi) {
        super(lo, hi);
        pages = new TreeMap<>(Long::compareUnsigned);
        cache = null;
        cache2 = null;
        cacheHits = 0;
        cacheMisses = 0;
        set64bit();
        setLE();
    }

    @TruffleBoundary
    public Collection<MemoryPage> getPages() {
        return Collections.unmodifiableCollection(pages.values());
    }

    @TruffleBoundary
    @Override
    public void add(MemoryPage page) {
        boolean ok = Long.compareUnsigned(page.end, pointerBase) <= 0 || Long.compareUnsigned(page.end, pointerEnd) > 0;
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
                    mapSequence++;
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
                    mapSequence++;
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
        mapSequence++;
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
                Memory buf = new ByteMemory(size, bigEndian);
                MemoryPage bufpage = new MemoryPage(buf, base, size, page.name);
                pages.put(base, bufpage);
                cache = null;
                cache2 = null;
                mapSequence++;
            }
        }
        if (DEBUG) {
            printLayout();
        }
    }

    @TruffleBoundary
    @Override
    public void remove(long addr, long len) throws PosixException {
        cache = null;
        cache2 = null;
        mapSequence++;
        long length = roundToPageSize(len);
        long address = addr(addr);
        if ((address & ~PAGE_MASK) != 0) {
            throw new PosixException(Errno.EINVAL);
        }
        try {
            for (long p = address; Long.compareUnsigned(p, address + length) < 0;) {
                MemoryPage page = getFloorEntry(p);
                if (p != page.base) { // split page and remove mapping in the middle
                    assert p > page.base;
                    if (DEBUG) {
                        CompilerDirectives.transferToInterpreter();
                        System.out.printf("Splitting old page: 0x%016X-0x%016X, removed page is 0x%016X-0x%016X\n", page.base, page.end, addr, addr + length);
                    }
                    pages.remove(page.base);
                    allocator.free(page.base, page.size);
                    long size1 = addr - page.base;
                    long size2 = page.end - (addr + length);
                    if (DEBUG) {
                        CompilerDirectives.transferToInterpreter();
                        System.out.printf("size1 = 0x%016X, size2 = 0x%016X\n", size1, size2);
                    }
                    if (size1 > 0) {
                        MemoryPage pag = new MemoryPage(page, page.base, size1);
                        pages.put(page.base, pag);
                        allocator.allocat(page.base, size1);
                        cache = null;
                        cache2 = null;
                        if (DEBUG) {
                            CompilerDirectives.transferToInterpreter();
                            System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X] (off=0x%x)\n", page.base, pages.get(page.base).base, pages.get(page.base).end, pag.getOffset(pag.base));
                        }
                    }
                    if (size2 > 0) {
                        MemoryPage pag = new MemoryPage(page, addr + length, size2);
                        pages.put(addr + length, pag);
                        allocator.allocat(addr + length, size2);
                        cache = null;
                        cache2 = null;
                        if (DEBUG) {
                            CompilerDirectives.transferToInterpreter();
                            System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X]\n", addr + length, pages.get(addr + length).base, pages.get(addr + length).end);
                        }
                    }
                    p = page.end;
                } else if (page.size > length) {
                    long sz = page.size - length;
                    MemoryPage tail = new MemoryPage(page, page.base + length, sz);
                    pages.remove(page.base);
                    allocator.free(page.base, length);
                    pages.put(page.base + length, tail);
                    allocator.allocat(page.base + length, sz);
                    p = page.end;
                } else {
                    pages.remove(page.base);
                    allocator.free(page.base, page.size);
                    p = page.end;
                }
            }
        } catch (SegmentationViolation e) {
            // swallow
        }
        if (DEBUG) {
            printLayout();
        }
    }

    @TruffleBoundary
    @Override
    public MemoryPage allocate(long size, String name) {
        long base = allocator.alloc(size);
        if (base == 0) {
            return null;
        } else {
            Memory mem = new ByteMemory(size, bigEndian);
            MemoryPage page = new MemoryPage(mem, base, size, name);
            add(page);
            return page;
        }
    }

    @TruffleBoundary
    @Override
    public MemoryPage allocate(Memory memory, long size, String name, long offset) {
        long base = allocator.alloc(size);
        if (base == 0) {
            return null;
        } else {
            MemoryPage page = new MemoryPage(memory, base, size, name, offset);
            add(page);
            return page;
        }
    }

    @Override
    public void free(long address) {
        MemoryPage page = pages.remove(address);
        allocator.free(address, page.size);
    }

    @TruffleBoundary
    private MemoryPage getFloorEntry(long addr) {
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

    @Override
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
        // slow path
        return getFloorEntry(addr);
    }

    @TruffleBoundary
    @Override
    public boolean contains(long address) {
        long addr = addr(address);
        Map.Entry<Long, MemoryPage> entry = pages.floorEntry(addr);
        if (entry == null) {
            return false;
        }
        MemoryPage page = entry.getValue();
        return page.contains(addr);
    }

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
    public void setI8(long address, byte val) {
        logMemoryWrite(address, 1, val);
        long ptr = addr(address);
        MemoryPage page = get(ptr);
        page.setI8(ptr, val);
    }

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
    public void setI128(long address, long hi, long lo) {
        setI128(address, new Vector128(hi, lo));
    }

    @TruffleBoundary
    @Override
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

    @TruffleBoundary
    @Override
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

    @Override
    public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
        long remaining = len;
        long p = address;
        while (remaining > 0) {
            MemoryPage page = get(p);
            if (page.base == addr(p) && Long.compareUnsigned(page.size, remaining) <= 0) {
                // whole "page"
                page.r = r;
                page.w = w;
                page.x = x;
                p = page.end;
                remaining -= page.size;
            } else if (page.base == addr(p) && Long.compareUnsigned(page.size, remaining) > 0) {
                // split, modify first part
                MemoryPage p1 = new MemoryPage(page, page.base, remaining);
                MemoryPage p2 = new MemoryPage(page, page.base + remaining, page.size - remaining);
                p1.r = r;
                p1.w = w;
                p1.x = x;
                pages.remove(page.base);
                pages.put(p1.base, p1);
                pages.put(p2.base, p2);
                return;
            } else {
                // split, modify second part
                assert Long.compareUnsigned(page.base, p) < 0;
                long off = p - page.base;
                MemoryPage p1 = new MemoryPage(page, page.base, off);
                MemoryPage p2 = new MemoryPage(page, page.base + off, page.size - off);
                p2.r = r;
                p2.w = w;
                p2.x = x;
                pages.remove(page.base);
                pages.put(p1.base, p1);
                pages.put(p2.base, p2);
                p = page.end;
                remaining -= page.size;
            }
        }
    }

    @Override
    public boolean isExecutable(long address) {
        MemoryPage page = get(address);
        return page.x;
    }

    @Override
    public void printMaps(PrintStream out) {
        CompilerAsserts.neverPartOfCompilation();
        pages.entrySet().stream().map((x) -> x.getValue().toString()).forEachOrdered(out::println);
    }

    public void printStats(PrintStream out) {
        CompilerAsserts.neverPartOfCompilation();
        out.printf("Cache: %d hits, %d misses (%5.3f%% hits)\n", cacheHits, cacheMisses,
                        (double) cacheHits / (double) (cacheHits + cacheMisses));
    }

    @Override
    public void printAddressInfo(long addr, PrintStream out) {
        MemoryPage page = get(addr);
        out.printf("Memory region name: '%s', base = 0x%016x (offset = 0x%016x)\n", page.name, page.base, addr - page.base);
    }
}
