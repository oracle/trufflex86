package org.graalvm.vm.memory.hardware;

import java.io.PrintStream;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
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
    public void add(MemoryPage page) {
        if (page.base < nmem.getVirtualHigh()) {
            if (page.end > nmem.getVirtualHigh()) {
                throw new IllegalArgumentException("cannot allocate page across different memory types");
            }
            nmem.add(page);
        } else {
            jmem.add(page);
        }
    }

    @Override
    public void remove(long addr, long len) throws PosixException {
        if (addr < nmem.getVirtualHigh()) {
            nmem.remove(addr, len);
        } else {
            jmem.remove(addr, len);
        }
    }

    @Override
    public MemoryPage allocate(long size) {
        return nmem.allocate(size);
    }

    @Override
    public MemoryPage allocate(Memory memory, long size, String name, long offset) {
        return jmem.allocate(memory, size, name, offset);
    }

    @Override
    public void free(long address) {
        jmem.free(address);
    }

    @Override
    public boolean contains(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.contains(address);
        } else {
            return jmem.contains(address);
        }
    }

    @Override
    public byte getI8(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI8(address);
        } else {
            return jmem.getI8(address);
        }
    }

    @Override
    public short getI16(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI16(address);
        } else {
            return jmem.getI16(address);
        }
    }

    @Override
    public int getI32(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI32(address);
        } else {
            return jmem.getI32(address);
        }
    }

    @Override
    public long getI64(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI64(address);
        } else {
            return jmem.getI64(address);
        }
    }

    @Override
    public Vector128 getI128(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI128(address);
        } else {
            return jmem.getI128(address);
        }
    }

    @Override
    public Vector256 getI256(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI256(address);
        } else {
            return jmem.getI256(address);
        }
    }

    @Override
    public Vector512 getI512(long address) {
        if (address < nmem.getVirtualHigh()) {
            return nmem.getI512(address);
        } else {
            return jmem.getI512(address);
        }
    }

    @Override
    public void setI8(long address, byte val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI8(address, val);
        } else {
            jmem.setI8(address, val);
        }
    }

    @Override
    public void setI16(long address, short val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI16(address, val);
        } else {
            jmem.setI16(address, val);
        }
    }

    @Override
    public void setI32(long address, int val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI32(address, val);
        } else {
            jmem.setI32(address, val);
        }
    }

    @Override
    public void setI64(long address, long val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI64(address, val);
        } else {
            jmem.setI64(address, val);
        }
    }

    @Override
    public void setI128(long address, Vector128 val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI128(address, val);
        } else {
            jmem.setI128(address, val);
        }
    }

    @Override
    public void setI128(long address, long hi, long lo) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI128(address, hi, lo);
        } else {
            jmem.setI128(address, hi, lo);
        }
    }

    @Override
    public void setI256(long address, Vector256 val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI256(address, val);
        } else {
            jmem.setI256(address, val);
        }
    }

    @Override
    public void setI512(long address, Vector512 val) {
        if (address < nmem.getVirtualHigh()) {
            nmem.setI512(address, val);
        } else {
            jmem.setI512(address, val);
        }
    }

    @Override
    public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
        if (address < nmem.getVirtualHigh()) {
            nmem.mprotect(address, len, r, w, x);
        } else {
            jmem.mprotect(address, len, r, w, x);
        }
    }

    @Override
    public void printLayout(PrintStream out) {
        nmem.printLayout(out);
        jmem.printLayout(out);
    }
}
