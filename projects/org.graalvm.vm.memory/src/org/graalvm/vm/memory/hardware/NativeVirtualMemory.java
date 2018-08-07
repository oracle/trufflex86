package org.graalvm.vm.memory.hardware;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixException;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;

public class NativeVirtualMemory extends VirtualMemory {
    private static final Logger log = Trace.create(NativeVirtualMemory.class);

    public static final long LOW = 0x200000000000L;
    public static final long HIGH = 0x400000000000L;
    public static final long SIZE = HIGH - LOW;

    private final long physicalLo;
    private final long physicalHi;

    private final long virtualLo;
    private final long virtualHi;

    private static boolean initialized = false;
    private static boolean supported;

    private static boolean checkMemoryMap() {
        try {
            MemoryMap map = new MemoryMap();

            for (MemorySegment s : map.getSegments()) {
                if (s.start >= LOW && s.start <= HIGH && s.end >= LOW && s.end <= HIGH) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isSupported() {
        if (!initialized) {
            supported = checkMemoryMap() && MMU.init(LOW, HIGH);
            initialized = true;
        }
        return supported;
    }

    public NativeVirtualMemory(long plo, long phi, long vlo, long vhi) {
        super(vlo + (vhi - vlo) / 2, vhi);
        this.physicalLo = plo;
        this.physicalHi = phi;
        this.virtualLo = vlo;
        this.virtualHi = vhi;
        long psz = physicalHi - physicalLo;
        long vsz = virtualHi - virtualLo;
        if (psz < vsz) {
            throw new IllegalArgumentException("physical area is too small");
        }
    }

    public long getPhysicalLow() {
        return physicalLo;
    }

    public long getPhysicalHigh() {
        return physicalHi;
    }

    public long getVirtualLow() {
        return virtualLo;
    }

    public long getVirtualHigh() {
        return virtualHi;
    }

    private void check(long address) {
        if (Long.compareUnsigned(address, virtualLo) >= 0 && Long.compareUnsigned(address, virtualHi) <= 0) {
            return;
        } else {
            CompilerDirectives.transferToInterpreter();
            throw new SegmentationViolation(address);
        }
    }

    private long phy(long address) {
        check(address);
        long phy = address - virtualLo + physicalLo;
        assert Long.compareUnsigned(phy, physicalLo) >= 0 && Long.compareUnsigned(phy, physicalHi) <= 0;
        return phy;
    }

    private static void checkSegfault(long address, long phy) {
        long segfault = MMU.getSegfaultAddress();
        if (segfault == phy) {
            CompilerDirectives.transferToInterpreter();
            throw new SegmentationViolation(address);
        }
    }

    public byte i8(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        byte value = NativeMemory.i8(phy);
        checkSegfault(address, phy);
        return value;
    }

    public short i16L(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        short value = NativeMemory.i16L(phy);
        checkSegfault(address, phy);
        return value;
    }

    public short i16B(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        short value = NativeMemory.i16B(phy);
        checkSegfault(address, phy);
        return value;
    }

    public int i32L(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        int value = NativeMemory.i32L(phy);
        checkSegfault(address, phy);
        return value;
    }

    public int i32B(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        int value = NativeMemory.i32B(phy);
        checkSegfault(address, phy);
        return value;
    }

    public long i64L(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        long value = NativeMemory.i64L(phy);
        checkSegfault(address, phy);
        return value;
    }

    public long i64B(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        long value = NativeMemory.i64B(phy);
        checkSegfault(address, phy);
        return value;
    }

    public void i8(long address, byte val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i8(phy, val);
        checkSegfault(address, phy);
    }

    public void i16L(long address, short val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i16L(phy, val);
        checkSegfault(address, phy);
    }

    public void i16B(long address, short val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i16B(phy, val);
        checkSegfault(address, phy);
    }

    public void i32L(long address, int val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i32L(phy, val);
        checkSegfault(address, phy);
    }

    public void i32B(long address, int val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i32B(phy, val);
        checkSegfault(address, phy);
    }

    public void i64L(long address, long val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i64L(phy, val);
        checkSegfault(address, phy);
    }

    public void i64B(long address, long val) {
        long addr = addr(address);
        long phy = phy(addr);
        NativeMemory.i64B(phy, val);
        checkSegfault(address, phy);
    }

    @Override
    public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
        long addr = addr(address);
        long phy = phy(addr);
        MMU.mprotect(phy, len, r, w, x);
    }

    @Override
    public void add(MemoryPage page) {
        Memory mem = page.getMemory();
        if (!(mem instanceof ByteMemory) && !(mem instanceof NullMemory)) {
            CompilerDirectives.transferToInterpreter();
            throw new IllegalArgumentException("not a ByteMemory");
        }
        boolean ok = Long.compareUnsigned(page.end, pointerBase) <= 0 || Long.compareUnsigned(page.end, pointerEnd) > 0;
        if (!ok) {
            allocator.allocat(page.base, page.size);
        }
        long addr = addr(page.base);
        long phy = phy(addr);
        long start = pageStart(phy);
        long headsz = phy - start;
        long size = roundToPageSize(page.size + headsz);
        try {
            MMU.mmap(start, size, page.r, true, page.x, true, true, false, -1, 0);
        } catch (PosixException e) {
            CompilerDirectives.transferToInterpreter();
            log.log(Levels.ERROR, "mmap failed: " + Errno.toString(e.getErrno()));
            throw new OutOfMemoryError("mmap failed: " + Errno.toString(e.getErrno()));
        }

        // System.out.printf("ADD: [0x%x-0x%x:0x%x] (0x%x-0x%x)\n", page.base, page.end, page.size,
        // start, start + size);

        // copy page content to native memory
        if (mem instanceof ByteMemory) {
            int i;
            for (i = 0; i < page.size - 8; i += 8) {
                long val = page.getI64(page.base + i);
                setI64(page.base + i, val);
            }
            for (; i < page.size; i++) {
                byte val = page.getI8(page.base + i);
                setI8(page.base + i, val);
            }
        }

        try {
            MMU.mprotect(start, size, page.r, page.w, page.x);
        } catch (PosixException e) {
            CompilerDirectives.transferToInterpreter();
            log.log(Levels.ERROR, "mprotect failed: " + Errno.toString(e.getErrno()));
            throw new OutOfMemoryError("mprotect failed: " + Errno.toString(e.getErrno()));
        }
    }

    @Override
    public void remove(long address, long len) throws PosixException {
        long addr = addr(address);
        allocator.free(address, len);
        long phy = phy(addr);
        MMU.munmap(phy, len);
    }

    @Override
    public MemoryPage allocate(long size) {
        long base = allocateRegion(size);
        MemoryPage page = new MemoryPage(new NullMemory(bigEndian, size), base, size);
        add(page);
        return page;
    }

    @Override
    public MemoryPage allocate(Memory memory, long size, String name, long offset) {
        if (!(memory instanceof ByteMemory)) {
            CompilerDirectives.transferToInterpreter();
            throw new IllegalArgumentException("not a ByteMemory");
        }

        long base = allocateRegion(size);
        MemoryPage page = new MemoryPage(memory, base, size);
        add(page);
        return page;
    }

    @Override
    public void free(long address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(long address) {
        long addr = addr(address);
        if (addr <= virtualLo || addr >= virtualHi) {
            return false;
        }
        long phy = phy(addr);
        NativeMemory.i8(phy);
        long segfault = MMU.getSegfaultAddress();
        return segfault == 0;
    }

    @Override
    public byte getI8(long address) {
        return i8(address);
    }

    @Override
    public short getI16(long address) {
        return bigEndian ? i16B(address) : i16L(address);
    }

    @Override
    public int getI32(long address) {
        return bigEndian ? i32B(address) : i32L(address);
    }

    @Override
    public long getI64(long address) {
        return bigEndian ? i64B(address) : i64L(address);
    }

    @Override
    public Vector128 getI128(long address) {
        if (bigEndian) {
            long hi = i64B(address);
            long lo = i64B(address + 8);
            return new Vector128(hi, lo);
        } else {
            long lo = i64L(address);
            long hi = i64L(address + 8);
            return new Vector128(hi, lo);
        }
    }

    @Override
    public Vector256 getI256(long address) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public Vector512 getI512(long address) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public void setI8(long address, byte val) {
        i8(address, val);
    }

    @Override
    public void setI16(long address, short val) {
        if (bigEndian) {
            i16B(address, val);
        } else {
            i16L(address, val);
        }
    }

    @Override
    public void setI32(long address, int val) {
        if (bigEndian) {
            i32B(address, val);
        } else {
            i32L(address, val);
        }
    }

    @Override
    public void setI64(long address, long val) {
        if (bigEndian) {
            i64B(address, val);
        } else {
            i64L(address, val);
        }
    }

    @Override
    public void setI128(long address, Vector128 val) {
        if (bigEndian) {
            i64B(address, val.getI64(0));
            i64B(address + 8, val.getI64(1));
        } else {
            i64L(address, val.getI64(1));
            i64L(address + 8, val.getI64(0));
        }
    }

    @Override
    public void setI128(long address, long hi, long lo) {
        if (bigEndian) {
            i64B(address, hi);
            i64B(address + 8, lo);
        } else {
            i64L(address, lo);
            i64L(address + 8, hi);
        }
    }

    @Override
    public void setI256(long address, Vector256 val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public void setI512(long address, Vector512 val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    private long vaddr(long phy) {
        long offset = phy - physicalLo;
        long vaddr = offset + virtualLo;
        return vaddr;
    }

    private MemorySegment segment(MemorySegment s) {
        return new MemorySegment(vaddr(s.start), vaddr(s.end), s.rawPermissions, s.offset, s.name);
    }

    @Override
    public void printLayout(PrintStream out) {
        CompilerAsserts.neverPartOfCompilation();
        out.println("Memory map:");
        try {
            MemoryMap map = new MemoryMap();

            for (MemorySegment s : map.getSegments()) {
                if (s.start >= physicalLo && s.start <= physicalHi && s.end >= physicalLo && s.end <= physicalHi) {
                    out.println(segment(s));
                }
            }
        } catch (IOException e) {
            e.printStackTrace(out);
        }
    }

    @Override
    public void printAddressInfo(long addr, PrintStream out) {
        try {
            MemoryMap map = new MemoryMap();
            for (MemorySegment s : map.getSegments()) {
                if (s.contains(phy(addr(addr)))) {
                    MemorySegment seg = segment(s);
                    out.printf("Memory region name: '%s', base = 0x%016x (offset = 0x%016x)\n", seg.name, seg.start, addr - seg.start);
                }
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Cannot retrieve memory region info", e);
        }
    }
}
