package org.graalvm.vm.memory;

import java.io.PrintStream;
import java.util.logging.Logger;

import org.graalvm.vm.memory.hardware.HybridVirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.posix.api.PosixException;
import com.everyware.posix.api.PosixPointer;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public abstract class VirtualMemory {
    private static final Logger log = Trace.create(VirtualMemory.class);

    protected static final boolean DEBUG = getBoolean("mem.debug", false);
    protected static final boolean DEBUG_1BYTE = getBoolean("mem.debug.1byte", true);

    public static final long PAGE_SIZE = 4096;
    public static final long PAGE_MASK = ~(PAGE_SIZE - 1);

    public static final long POINTER_BASE = 0x00007f0000000000L;
    public static final long POINTER_END = 0x00007fff00000000L;

    protected final long pointerBase;
    protected final long pointerEnd;

    protected final MemoryAllocator allocator;

    @CompilationFinal protected long mask;

    protected long brk;
    protected long reportedBrk;

    @CompilationFinal protected boolean debugMemory;
    @CompilationFinal protected boolean debugSingleByte;

    @CompilationFinal protected boolean bigEndian;

    protected long mapSequence;

    public static final VirtualMemory create() {
        if (DEBUG) {
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
        debugMemory = DEBUG;
        debugSingleByte = DEBUG_1BYTE;
        mapSequence = 0;
        set64bit();
        setLE();
    }

    public static boolean getBoolean(String name, boolean fallback) {
        String value = System.getProperty(name, Boolean.toString(fallback));
        return value.equalsIgnoreCase("true") || value.equals("1");
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

    public abstract void add(MemoryPage page);

    public abstract void remove(long addr, long len) throws PosixException;

    public abstract MemoryPage allocate(long size);

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

    public abstract void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException;

    public void dump(long p, int size) {
        CompilerAsserts.neverPartOfCompilation();
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

    protected void logMemoryRead(long address, int size) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read %d bytes\n", addr(addr), size);
        }
    }

    protected void logMemoryRead(long address, int size, long value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i64(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryRead(long address, int size, int value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i32(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%08x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%08x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryRead(long address, int size, short value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i16(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%04x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: read %d bytes (0x%04x)\n", addr(addr), size, value);
            }
        }
    }

    private static boolean isPrintable(byte value) {
        return value >= 0x20 && value <= 0x7e; // ascii
    }

    protected void logMemoryRead(long address, int size, byte value) {
        if (debugMemory && debugSingleByte) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            if (isPrintable(value)) {
                System.out.printf("Memory access to 0x%016x: read %d byte(s) (0x%02x, '%c')\n", addr(addr), size, value, new Character((char) (value & 0x7F)));
            } else {
                System.out.printf("Memory access to 0x%016x: read %d byte(s) (0x%02x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryRead(long address, Vector128 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 16 bytes (%s)\n", addr(addr), value);
        }
    }

    protected void logMemoryRead(long address, Vector256 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 32 bytes (%s)\n", addr(addr), value);
        }
    }

    protected void logMemoryRead(long address, Vector512 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: read 64 bytes (%s)\n", addr(addr), value);
        }
    }

    protected void logMemoryWrite(long address, int size, long value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i64(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryWrite(long address, int size, int value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i32(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%08x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%08x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryWrite(long address, int size, short value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            String val = Stringify.i16(value);
            if (val != null) {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%04x, '%s')\n", addr(addr), size, value, val);
            } else {
                System.out.printf("Memory access to 0x%016x: write %d bytes (0x%04x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryWrite(long address, int size, byte value) {
        if (debugMemory && debugSingleByte) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            if (isPrintable(value)) {
                System.out.printf("Memory access to 0x%016x: write %d byte(s) (0x%02x, '%c')\n", addr(addr), size, value, new Character((char) (value & 0x7F)));
            } else {
                System.out.printf("Memory access to 0x%016x: write %d byte(s) (0x%02x)\n", addr(addr), size, value);
            }
        }
    }

    protected void logMemoryWrite(long address, Vector128 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 16 bytes (%s)\n", addr(addr), value);
        }
    }

    protected void logMemoryWrite(long address, Vector256 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 32 bytes (%s)\n", addr(addr), value);
        }
    }

    protected void logMemoryWrite(long address, Vector512 value) {
        if (debugMemory) {
            CompilerDirectives.transferToInterpreter();
            long addr = addr(address);
            System.out.printf("Memory access to 0x%016x: write 64 bytes (%s)\n", addr(addr), value);
        }
    }

    public void printLayout() {
        printLayout(System.out);
    }

    public abstract void printLayout(PrintStream out);
}
