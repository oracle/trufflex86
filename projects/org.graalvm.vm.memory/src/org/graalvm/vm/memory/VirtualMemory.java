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
package org.graalvm.vm.memory;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.graalvm.vm.memory.hardware.HybridVirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.util.log.Trace;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public abstract class VirtualMemory {
    private static final Logger log = Trace.create(VirtualMemory.class);

    protected static final boolean DEBUG = MemoryOptions.MEM_DEBUG.get();
    protected static final boolean VIRTUAL = MemoryOptions.MEM_VIRTUAL.get();
    protected static final boolean VERIFY = MemoryOptions.MEM_VERIFY.get();
    protected static final boolean MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    public static final long PAGE_SIZE = 4096;
    public static final long PAGE_MASK = ~(PAGE_SIZE - 1);

    public static final long POINTER_BASE = 0x00007f0000000000L;
    public static final long POINTER_END = 0x00007fff00000000L;

    public static final long MAPPED_NATIVE_BIT = 1L << 63;

    protected final long pointerBase;
    protected final long pointerEnd;

    protected final MemoryAllocator allocator;

    @CompilationFinal protected long mask;

    protected long brk;
    protected long reportedBrk;

    @CompilationFinal protected boolean enableAccessTrace;

    @CompilationFinal protected boolean bigEndian;

    protected long mapSequence;

    @CompilationFinal MemoryAccessListener logger;

    public static final VirtualMemory create() {
        if (VERIFY) {
            if (!NativeVirtualMemory.isSupported()) {
                log.warning("native memory not supported, therefore virtual memory verification is impossible");
            } else {
                log.info("using virtual memory verifier");
                return new VirtualMemoryVerifier();
            }
        }
        if (VIRTUAL) {
            return new JavaVirtualMemory();
        } else if (NativeVirtualMemory.isSupported()) {
            return new HybridVirtualMemory();
        } else {
            log.warning("PERF WARN: native memory not supported, falling back to Java based MMU emulation");
            return new JavaVirtualMemory();
        }
    }

    public VirtualMemory() {
        this(POINTER_BASE, POINTER_END);
    }

    public VirtualMemory(long pointerBase, long pointerEnd) {
        this.pointerBase = pointerBase;
        this.pointerEnd = pointerEnd;
        allocator = new MemoryAllocator(pointerBase, pointerEnd - pointerBase);
        brk = 0;
        reportedBrk = brk;
        enableAccessTrace = DEBUG;
        mapSequence = 0;
        logger = new DefaultMemoryAccessLogger();
        set64bit();
        setLE();
    }

    public void setAccessLogger(MemoryAccessListener logger) {
        this.logger = logger;
        enableAccessTrace = true;
    }

    public void set32bit() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        mask = 0x00000000FFFFFFFFL;
    }

    public void set64bit() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        mask = 0xFFFFFFFFFFFFFFFFL;
    }

    public void setLE() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        bigEndian = false;
    }

    public void setBE() {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        bigEndian = true;
    }

    public long toMappedNative(long address) {
        if (address == 0) {
            return 0;
        } else {
            return address | MAPPED_NATIVE_BIT;
        }
    }

    public static final long fromMappedNative(long address) {
        return address & ~MAPPED_NATIVE_BIT;
    }

    public static final boolean isMappedNative(long address) {
        return MAP_NATIVE && address < 0;
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
        if (Long.compareUnsigned(addr, brk) > 0 && Long.compareUnsigned(addr, pointerBase) <= 0) {
            long sz = roundToPageSize(addr - brk);
            Memory mem = new ByteMemory(sz, bigEndian);
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

    public long getMapSequence() {
        return mapSequence;
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

    public long getNativeAddress(long address) {
        // Note: some memory implementations like PtraceVirtualMemory or
        // JavaVirtualMemory cannot provide a native address
        return address;
    }

    public MemoryPage get(@SuppressWarnings("unused") long addr) {
        return null;
    }

    public abstract void add(MemoryPage page);

    public abstract void remove(long addr, long len) throws PosixException;

    public MemoryPage allocate(long size) {
        return allocate(size, null);
    }

    public abstract MemoryPage allocate(long size, String name);

    public MemoryPage allocate(Memory memory, long size, String name) {
        return allocate(memory, size, name, 0);
    }

    public abstract MemoryPage allocate(Memory memory, long size, String name, long offset);

    public abstract void free(long address);

    protected long allocateRegion(long size) {
        return allocator.alloc(size);
    }

    protected void free(long address, long size) {
        allocator.free(address, size);
    }

    public void printAccessError(long addr, MemoryPage page) {
        CompilerAsserts.neverPartOfCompilation();
        if (page != null) {
            System.err.printf("Tried to access 0x%016X, nearest page is P[0x%016X;0x%016X]\n", addr, page.base, page.end);
        } else {
            System.err.printf("Tried to access 0x%016X\n", addr);
        }
        printLayout(System.err);
    }

    public PosixPointer getPosixPointer(long address) {
        long addr = addr(address);
        return new PosixVirtualMemoryPointer(this, addr);
    }

    public abstract boolean contains(long address);

    public abstract byte getI8(long address);

    public abstract short getI16(long address);

    public abstract int getI32(long address);

    public abstract long getI64(long address);

    public abstract Vector128 getI128(long address);

    public abstract Vector256 getI256(long address);

    public abstract Vector512 getI512(long address);

    public abstract void setI8(long address, byte val);

    public abstract void setI16(long address, short val);

    public abstract void setI32(long address, int val);

    public abstract void setI64(long address, long val);

    public abstract void setI128(long address, Vector128 val);

    public abstract void setI128(long address, long hi, long lo);

    public abstract void setI256(long address, Vector256 val);

    public abstract void setI512(long address, Vector512 val);

    public abstract boolean cmpxchgI8(long address, byte expected, byte x);

    public abstract boolean cmpxchgI16(long address, short expected, short x);

    public abstract boolean cmpxchgI32(long address, int expected, int x);

    public abstract boolean cmpxchgI64(long address, long expected, long x);

    public abstract boolean cmpxchgI128(long address, Vector128 expected, Vector128 x);

    public abstract void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException;

    public abstract boolean isExecutable(long address);

    public byte peek(long p) {
        // disable memory access log during peek
        boolean wasDebug = enableAccessTrace;
        enableAccessTrace = false;
        try {
            return getI8(p);
        } finally {
            enableAccessTrace = wasDebug;
        }
    }

    public void dump(long p, int size) {
        CompilerAsserts.neverPartOfCompilation();
        // disable memory access log during dump
        boolean wasDebug = enableAccessTrace;
        enableAccessTrace = false;
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
            enableAccessTrace = wasDebug;
        }
    }

    protected void logMemoryRead(long address, int size) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryRead(addr(address), size);
        }
    }

    protected void logMemoryRead(long address, int size, long value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryRead(addr(address), size, value);
        }
    }

    private static boolean isPrintable(byte value) {
        return value >= 0x20 && value <= 0x7e; // ascii
    }

    protected void logMemoryRead(long address, Vector128 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryRead(addr(address), value);
        }
    }

    protected void logMemoryRead(long address, Vector256 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryRead(addr(address), value);
        }
    }

    protected void logMemoryRead(long address, Vector512 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryRead(addr(address), value);
        }
    }

    protected void logMemoryWrite(long address, int size, long value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryWrite(addr(address), size, value);
        }
    }

    protected void logMemoryWrite(long address, Vector128 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryWrite(addr(address), value);
        }
    }

    protected void logMemoryWrite(long address, Vector256 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryWrite(addr(address), value);
        }
    }

    protected void logMemoryWrite(long address, Vector512 value) {
        if (enableAccessTrace && logger != null) {
            CompilerAsserts.neverPartOfCompilation();
            logger.logMemoryWrite(addr(address), value);
        }
    }

    public void printLayout() {
        printLayout(System.out);
    }

    public void printLayout(PrintStream out) {
        out.println("Memory map:");
        printMaps(out);
    }

    public abstract void printMaps(PrintStream out);

    public abstract void printAddressInfo(long address, PrintStream out);
}
