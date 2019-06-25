/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.memory.hardware;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixMemory;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class NativeVirtualMemory extends VirtualMemory {
    private static final Logger log = Trace.create(NativeVirtualMemory.class);

    private static final String ARCH = System.getProperty("os.arch");
    private static final boolean BYPASS_SEGFAULT_CHECK = MemoryOptions.BYPASS_SEGFAULT_CHECK.get();

    public static final long LOW = getLow();
    public static final long HIGH = getHigh();
    public static final long SIZE = HIGH - LOW;

    private final long physicalLo;
    private final long physicalHi;

    private final long virtualLo;
    private final long virtualHi;

    private static boolean initialized = false;
    private static boolean supported;

    private List<MemorySegment> map;

    private static long getLow() {
        switch (ARCH) {
            case "aarch64":
                return 0x2000000000L;
            default:
                return 0x200000000000L;
        }
    }

    private static long getHigh() {
        switch (ARCH) {
            case "aarch64":
                return 0x4000000000L;
            default:
                return 0x400000000000L;
        }
    }

    @TruffleBoundary
    private static boolean checkMemoryMap() {
        try {
            MemoryMap map = new MemoryMap();

            for (MemorySegment s : map.getSegments()) {
                if (Long.compareUnsigned(s.start, LOW) >= 0 && Long.compareUnsigned(s.start, HIGH) <= 0 && Long.compareUnsigned(s.end, LOW) >= 0 && Long.compareUnsigned(s.end, HIGH) <= 0) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            log.log(Level.INFO, "memory region for native memory already mapped");
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
        updateMemoryMap();
        reset();
        if (BYPASS_SEGFAULT_CHECK) {
            log.warning("Ignoring segfaults in guest program");
        }
    }

    // unmap native memory and then map it again to clear all previous mappings and initialize the
    // memory to all zeros
    public void reset() {
        try {
            MMU.munmap(physicalLo, physicalHi - physicalLo);
            MMU.mmap(physicalLo, physicalHi - physicalLo, false, false, false, true, true, false, -1, 0);
        } catch (PosixException e) {
            log.log(Levels.ERROR, "Error while initializing native memory: " + e);
        }
        updateMemoryMap();
    }

    @TruffleBoundary
    private List<MemorySegment> getMemoryMap() throws IOException {
        List<MemorySegment> segments = new ArrayList<>();
        MemoryMap memoryMap = new MemoryMap();
        for (MemorySegment s : memoryMap.getSegments()) {
            if (Long.compareUnsigned(s.start, physicalLo) >= 0 && Long.compareUnsigned(s.start, physicalHi) <= 0 && Long.compareUnsigned(s.end, physicalLo) >= 0 &&
                            Long.compareUnsigned(s.end, physicalHi) <= 0) {
                segments.add(s);
            }
        }
        return segments;
    }

    private void updateMemoryMap() {
        try {
            map = getMemoryMap();
        } catch (IOException e) {
            CompilerDirectives.transferToInterpreter();
            log.log(Level.WARNING, "Cannot retrieve memory region info", e);
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
        if (BYPASS_SEGFAULT_CHECK) {
            return;
        }
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
    public long getNativeAddress(long address) {
        long addr = addr(address);
        long phy = phy(addr);
        return phy;
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
        if (mem instanceof PosixMemory) {
            assert ((PosixMemory) mem).isReadOnly();

            // copy
            long size = mem.size();
            Memory memory = new ByteMemory(size, false);
            long sz = mem.size();
            for (int i = 0; i < sz; i++) {
                memory.setI8(i, mem.getI8(i));
            }
            MemoryPage pag = new MemoryPage(memory, page.base, page.size, page.name, page.fileOffset);
            add(pag);
            return;
        } else if (!(mem instanceof ByteMemory) && !(mem instanceof NullMemory)) {
            throw new IllegalArgumentException("not a ByteMemory");
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
            log.log(Levels.WARNING, "mmap failed: " + Errno.toString(e.getErrno()));
            throw new OutOfMemoryError("mmap failed: " + Errno.toString(e.getErrno()));
        }

        // update allocator here, otherwise failed mmap cannot be rolled back in allocator
        boolean ok = Long.compareUnsigned(page.end, pointerBase) <= 0 || Long.compareUnsigned(page.end, pointerEnd) > 0;
        if (!ok) {
            allocator.allocat(page.base, page.size);
        }

        // System.out.printf("ADD: [0x%x-0x%x:0x%x] (0x%x-0x%x)\n", page.base, page.end, page.size,
        // start, start + size);

        // copy page content to native memory
        if (mem instanceof ByteMemory) {
            int i = 0;
            try {
                for (i = 0; i < page.size - 8; i += 8) {
                    long val = page.getI64(page.base + i);
                    setI64(page.base + i, val);
                }
            } catch (SegmentationViolation e) {
                // this could be a SIGBUS when accessing a mmap'd file
            }
            try {
                for (; i < page.size; i++) {
                    byte val = page.getI8(page.base + i);
                    setI8(page.base + i, val);
                }
            } catch (SegmentationViolation e) {
                // this could be a SIGBUS when accessing a mmap'd file
            }
        } else {
            // clear
            int i = 0;
            try {
                for (i = 0; i < page.size - 8; i += 8) {
                    setI64(page.base + i, 0);
                }
            } catch (SegmentationViolation e) {
                // this could be a SIGBUS when accessing a mmap'd file
            }
            try {
                for (; i < page.size; i++) {
                    setI8(page.base + i, (byte) 0);
                }
            } catch (SegmentationViolation e) {
                // this could be a SIGBUS when accessing a mmap'd file
            }
        }

        try {
            MMU.mprotect(start, size, page.r, page.w, page.x);
        } catch (PosixException e) {
            CompilerDirectives.transferToInterpreter();
            log.log(Levels.WARNING, "mprotect failed: " + Errno.toString(e.getErrno()));
            throw new OutOfMemoryError("mprotect failed: " + Errno.toString(e.getErrno()));
        }

        updateMemoryMap();
    }

    @Override
    public void remove(long address, long len) throws PosixException {
        long addr = addr(address);
        allocator.free(address, len);
        long phy = phy(addr);
        MMU.munmap(phy, len);

        updateMemoryMap();
    }

    @Override
    public MemoryPage allocate(long size, String name) {
        long base = allocateRegion(size);
        MemoryPage page = new MemoryPage(new NullMemory(bigEndian, size), base, size, name);
        add(page);
        return page;
    }

    @Override
    public MemoryPage allocate(Memory memory, long size, String name, long offset) {
        if (memory instanceof PosixMemory) {
            assert ((PosixMemory) memory).isReadOnly();

            // copy
            Memory mem = new ByteMemory(size, false);
            long sz = memory.size();
            for (int i = 0; i < sz; i++) {
                mem.setI8(i, memory.getI8(i));
            }
            return allocate(mem, size, name, offset);
        } else if (!(memory instanceof ByteMemory)) {
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

    @Override
    public boolean cmpxchgI8(long address, byte expected, byte x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI8(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    public boolean cmpxchgI16B(long address, short expected, short x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI16B(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    public boolean cmpxchgI16L(long address, short expected, short x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI16L(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    @Override
    public boolean cmpxchgI16(long address, short expected, short x) {
        if (bigEndian) {
            return cmpxchgI16B(address, expected, x);
        } else {
            return cmpxchgI16L(address, expected, x);
        }
    }

    public boolean cmpxchgI32B(long address, int expected, int x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI32B(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    public boolean cmpxchgI32L(long address, int expected, int x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI32L(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    @Override
    public boolean cmpxchgI32(long address, int expected, int x) {
        if (bigEndian) {
            return cmpxchgI32B(address, expected, x);
        } else {
            return cmpxchgI32L(address, expected, x);
        }
    }

    public boolean cmpxchgI64B(long address, long expected, long x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI64B(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    public boolean cmpxchgI64L(long address, long expected, long x) {
        long addr = addr(address);
        long phy = phy(addr);
        boolean value = NativeMemory.cmpxchgI64L(phy, expected, x);
        checkSegfault(address, phy);
        return value;
    }

    @Override
    public boolean cmpxchgI64(long address, long expected, long x) {
        if (bigEndian) {
            return cmpxchgI64B(address, expected, x);
        } else {
            return cmpxchgI64L(address, expected, x);
        }
    }

    @Override
    public boolean cmpxchgI128(long address, Vector128 expected, Vector128 x) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExecutable(long address) {
        for (MemorySegment s : map) {
            if (s.contains(phy(addr(address)))) {
                return s.permissions.isExecute();
            }
        }
        return false;
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
    public void printMaps(PrintStream out) {
        CompilerAsserts.neverPartOfCompilation();
        for (MemorySegment s : map) {
            out.println(segment(s));
        }
    }

    @Override
    public void printAddressInfo(long addr, PrintStream out) {
        for (MemorySegment s : map) {
            if (s.contains(phy(addr(addr)))) {
                MemorySegment seg = segment(s);
                out.printf("Memory region name: '%s', base = 0x%016x (offset = 0x%016x)\n", seg.name, seg.start, addr - seg.start);
            }
        }
    }
}
