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

import java.io.PrintStream;
import java.nio.ByteOrder;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixMemory;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.util.UnsafeHolder;

import sun.misc.Unsafe;

public class HybridVirtualMemory extends VirtualMemory {
    private final JavaVirtualMemory jmem;
    private final NativeVirtualMemory nmem;

    private final Unsafe unsafe = MAP_NATIVE ? UnsafeHolder.getUnsafe() : null;

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
    public long toMappedNative(long address) {
        if (Long.compareUnsigned(address, nmem.getPhysicalLow()) >= 0 && Long.compareUnsigned(address, nmem.getPhysicalHigh()) <= 0) {
            return address - nmem.getPhysicalLow() + nmem.getVirtualLow();
        } else {
            return super.toMappedNative(address);
        }
    }

    @Override
    public long getNativeAddress(long addr) {
        if (Long.compareUnsigned(addr, nmem.getVirtualHigh()) >= 0) {
            return jmem.getNativeAddress(addr);
        } else {
            return nmem.getNativeAddress(addr);
        }
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
        if (MAP_NATIVE && address < 0) {
            return true;
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.contains(address);
        } else {
            return jmem.contains(address);
        }
    }

    @Override
    public boolean isExecutable(long address) {
        if (MAP_NATIVE && address < 0) {
            return false; // mapped host process memory is *not* executable
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.isExecutable(address);
        } else {
            return jmem.isExecutable(address);
        }
    }

    @Override
    public byte getI8(long address) {
        if (MAP_NATIVE && address < 0) {
            return unsafe.getByte(fromMappedNative(address));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI8(address);
        } else {
            return jmem.getI8(address);
        }
    }

    @Override
    public short getI16(long address) {
        if (MAP_NATIVE && address < 0) {
            return unsafe.getShort(fromMappedNative(address));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI16(address);
        } else {
            return jmem.getI16(address);
        }
    }

    @Override
    public int getI32(long address) {
        if (MAP_NATIVE && address < 0) {
            return unsafe.getInt(fromMappedNative(address));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI32(address);
        } else {
            return jmem.getI32(address);
        }
    }

    @Override
    public long getI64(long address) {
        if (MAP_NATIVE && address < 0) {
            return unsafe.getLong(fromMappedNative(address));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI64(address);
        } else {
            return jmem.getI64(address);
        }
    }

    @Override
    public Vector128 getI128(long address) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            long lo = unsafe.getLong(base);
            long hi = unsafe.getLong(base + 8);
            return new Vector128(hi, lo);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI128(address);
        } else {
            return jmem.getI128(address);
        }
    }

    @Override
    public Vector256 getI256(long address) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            long i4 = unsafe.getLong(base);
            long i3 = unsafe.getLong(base + 8);
            long i2 = unsafe.getLong(base + 16);
            long i1 = unsafe.getLong(base + 24);
            return new Vector256(new long[]{i1, i2, i3, i4});
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI256(address);
        } else {
            return jmem.getI256(address);
        }
    }

    @Override
    public Vector512 getI512(long address) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            long i8 = unsafe.getLong(base);
            long i7 = unsafe.getLong(base + 8);
            long i6 = unsafe.getLong(base + 16);
            long i5 = unsafe.getLong(base + 24);
            long i4 = unsafe.getLong(base + 32);
            long i3 = unsafe.getLong(base + 40);
            long i2 = unsafe.getLong(base + 48);
            long i1 = unsafe.getLong(base + 56);
            return new Vector512(new long[]{i1, i2, i3, i4, i5, i6, i7, i8});
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.getI512(address);
        } else {
            return jmem.getI512(address);
        }
    }

    @Override
    public void setI8(long address, byte val) {
        if (MAP_NATIVE && address < 0) {
            unsafe.putByte(fromMappedNative(address), val);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI8(address, val);
        } else {
            jmem.setI8(address, val);
        }
    }

    @Override
    public void setI16(long address, short val) {
        if (MAP_NATIVE && address < 0) {
            unsafe.putShort(fromMappedNative(address), val);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI16(address, val);
        } else {
            jmem.setI16(address, val);
        }
    }

    @Override
    public void setI32(long address, int val) {
        if (MAP_NATIVE && address < 0) {
            unsafe.putInt(fromMappedNative(address), val);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI32(address, val);
        } else {
            jmem.setI32(address, val);
        }
    }

    @Override
    public void setI64(long address, long val) {
        if (MAP_NATIVE && address < 0) {
            unsafe.putLong(fromMappedNative(address), val);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI64(address, val);
        } else {
            jmem.setI64(address, val);
        }
    }

    @Override
    public void setI128(long address, Vector128 val) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            unsafe.putLong(base, val.getI64(1));
            unsafe.putLong(base + 8, val.getI64(0));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI128(address, val);
        } else {
            jmem.setI128(address, val);
        }
    }

    @Override
    public void setI128(long address, long hi, long lo) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            unsafe.putLong(base, lo);
            unsafe.putLong(base + 8, hi);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI128(address, hi, lo);
        } else {
            jmem.setI128(address, hi, lo);
        }
    }

    @Override
    public void setI256(long address, Vector256 val) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            unsafe.putLong(base, val.getI64(3));
            unsafe.putLong(base + 8, val.getI64(2));
            unsafe.putLong(base + 16, val.getI64(1));
            unsafe.putLong(base + 24, val.getI64(0));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI256(address, val);
        } else {
            jmem.setI256(address, val);
        }
    }

    @Override
    public void setI512(long address, Vector512 val) {
        if (MAP_NATIVE && address < 0) {
            long base = fromMappedNative(address);
            unsafe.putLong(base, val.getI64(7));
            unsafe.putLong(base + 8, val.getI64(6));
            unsafe.putLong(base + 16, val.getI64(5));
            unsafe.putLong(base + 24, val.getI64(4));
            unsafe.putLong(base + 32, val.getI64(3));
            unsafe.putLong(base + 40, val.getI64(2));
            unsafe.putLong(base + 48, val.getI64(1));
            unsafe.putLong(base + 56, val.getI64(0));
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            nmem.setI512(address, val);
        } else {
            jmem.setI512(address, val);
        }
    }

    @Override
    public boolean cmpxchgI8(long address, byte expected, byte x) {
        if (MAP_NATIVE && address < 0) {
            long addr = fromMappedNative(address);
            int val = unsafe.getInt(addr);
            int v;
            int exp;
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                v = (val & 0x00FFFFFF) | (Byte.toUnsignedInt(x) << 24);
                exp = (val & 0x00FFFFFF) | (Byte.toUnsignedInt(expected) << 24);
            } else {
                v = (val & 0xFFFFFF00) | Byte.toUnsignedInt(x);
                exp = (val & 0xFFFFFF00) | Byte.toUnsignedInt(expected);
            }
            return unsafe.compareAndSwapInt(null, addr, exp, v);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.cmpxchgI8(address, expected, x);
        } else {
            return jmem.cmpxchgI8(address, expected, x);
        }
    }

    @Override
    public boolean cmpxchgI16(long address, short expected, short x) {
        if (MAP_NATIVE && address < 0) {
            long addr = fromMappedNative(address);
            int val = unsafe.getInt(addr);
            int v;
            int exp;
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                v = (val & 0x0000FFFF) | (Short.toUnsignedInt(x) << 16);
                exp = (val & 0x0000FFFF) | (Short.toUnsignedInt(expected) << 16);
            } else {
                v = (val & 0xFFFF0000) | Short.toUnsignedInt(x);
                exp = (val & 0xFFFF0000) | Short.toUnsignedInt(expected);
            }
            return unsafe.compareAndSwapInt(null, addr, exp, v);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.cmpxchgI16(address, expected, x);
        } else {
            return jmem.cmpxchgI16(address, expected, x);
        }
    }

    @Override
    public boolean cmpxchgI32(long address, int expected, int x) {
        if (MAP_NATIVE && address < 0) {
            long addr = fromMappedNative(address);
            return unsafe.compareAndSwapInt(null, addr, expected, x);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.cmpxchgI32(address, expected, x);
        } else {
            return jmem.cmpxchgI32(address, expected, x);
        }
    }

    @Override
    public boolean cmpxchgI64(long address, long expected, long x) {
        if (MAP_NATIVE && address < 0) {
            long addr = fromMappedNative(address);
            return unsafe.compareAndSwapLong(null, addr, expected, x);
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
            return nmem.cmpxchgI64(address, expected, x);
        } else {
            return jmem.cmpxchgI64(address, expected, x);
        }
    }

    // TODO: this is still not atomic!
    @Override
    public boolean cmpxchgI128(long address, Vector128 expected, Vector128 x) {
        setI128(address, x);
        return true;
    }

    @Override
    public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
        if (MAP_NATIVE && address < 0) {
            return; // no mprotect for mapped host process memory
        } else if (Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0) {
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
