package org.graalvm.vm.memory.hardware;

import java.io.PrintStream;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixMemory;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.everyware.posix.api.PosixException;

public class HybridVirtualMemory extends VirtualMemory {
    private final JavaVirtualMemory jmem;
    private final NativeVirtualMemory nmem;

    public HybridVirtualMemory() {
        jmem = new JavaVirtualMemory();
        nmem = new NativeVirtualMemory(NativeVirtualMemory.LOW, NativeVirtualMemory.HIGH, 0, NativeVirtualMemory.SIZE);
    }

    public NativeVirtualMemory getNativeVirtualMemory() {
        return nmem;
    }

    public JavaVirtualMemory getJavaVirtualMemory() {
        return jmem;
    }

    @Override
    public MemoryPage get(long addr) {
        if (Long.compareUnsigned(addr, nmem.getVirtualHigh()) >= 0) {
            return jmem.get(addr);
        } else {
            return null;
        }
    }

    @Override
    public void add(MemoryPage page) {
        if (Long.compareUnsigned(page.base, nmem.getVirtualHigh()) < 0) {
            if (Long.compareUnsigned(page.end, nmem.getVirtualHigh()) > 0) {
                throw new IllegalArgumentException("cannot allocate page across different memory types");
            }
            nmem.add(page);
        } else {
            jmem.add(page);
        }
    }

    @Override
    public void remove(long addr, long len) throws PosixException {
        if (Long.compareUnsigned(addr, nmem.getVirtualHigh()) < 0) {
            nmem.remove(addr, len);
        } else {
            jmem.remove(addr, len);
        }
    }

    @Override
    public MemoryPage allocate(long size, String name) {
        return nmem.allocate(size, name);
    }

    @Override
    public MemoryPage allocate(Memory memory, long size, String name, long offset) {
        if (memory instanceof PosixMemory && ((PosixMemory) memory).isReadOnly()) {
            return nmem.allocate(memory, size, name, offset);
        } else {
            return jmem.allocate(memory, size, name, offset);
        }
    }

    @Override
    public void free(long address) {
        jmem.free(address);
    }

    @Override
    public boolean contains(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.contains(address);
        } else {
            return jmem.contains(address);
        }
    }

    @Override
    public boolean isExecutable(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.isExecutable(address);
        } else {
            return jmem.isExecutable(address);
        }
    }

    @Override
    public byte getI8(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI8(address);
        } else {
            return jmem.getI8(address);
        }
    }

    @Override
    public short getI16(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI16(address);
        } else {
            return jmem.getI16(address);
        }
    }

    @Override
    public int getI32(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI32(address);
        } else {
            return jmem.getI32(address);
        }
    }

    @Override
    public long getI64(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI64(address);
        } else {
            return jmem.getI64(address);
        }
    }

    @Override
    public Vector128 getI128(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI128(address);
        } else {
            return jmem.getI128(address);
        }
    }

    @Override
    public Vector256 getI256(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI256(address);
        } else {
            return jmem.getI256(address);
        }
    }

    @Override
    public Vector512 getI512(long address) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI512(address);
        } else {
            return jmem.getI512(address);
        }
    }

    @Override
    public void setI8(long address, byte val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI8(address, val);
        } else {
            jmem.setI8(address, val);
        }
    }

    @Override
    public void setI16(long address, short val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI16(address, val);
        } else {
            jmem.setI16(address, val);
        }
    }

    @Override
    public void setI32(long address, int val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI32(address, val);
        } else {
            jmem.setI32(address, val);
        }
    }

    @Override
    public void setI64(long address, long val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI64(address, val);
        } else {
            jmem.setI64(address, val);
        }
    }

    @Override
    public void setI128(long address, Vector128 val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI128(address, val);
        } else {
            jmem.setI128(address, val);
        }
    }

    @Override
    public void setI128(long address, long hi, long lo) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI128(address, hi, lo);
        } else {
            jmem.setI128(address, hi, lo);
        }
    }

    @Override
    public void setI256(long address, Vector256 val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI256(address, val);
        } else {
            jmem.setI256(address, val);
        }
    }

    @Override
    public void setI512(long address, Vector512 val) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI512(address, val);
        } else {
            jmem.setI512(address, val);
        }
    }

    @Override
    public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.mprotect(address, len, r, w, x);
        } else {
            jmem.mprotect(address, len, r, w, x);
        }
    }

    @Override
    public void printMaps(PrintStream out) {
        nmem.printMaps(out);
        jmem.printMaps(out);
    }

    @Override
    public void printAddressInfo(long address, PrintStream out) {
        if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.printAddressInfo(address, out);
        } else {
            jmem.printAddressInfo(address, out);
        }
    }
}
