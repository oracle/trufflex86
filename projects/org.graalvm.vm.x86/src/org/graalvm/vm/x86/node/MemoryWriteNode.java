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
package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.HybridVirtualMemory;
import org.graalvm.vm.memory.hardware.NativeMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.util.UnsafeHolder;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNode.HybridMemoryCmpxchgI16Node;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNode.HybridMemoryCmpxchgI32Node;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNode.HybridMemoryCmpxchgI64Node;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNode.HybridMemoryCmpxchgI8Node;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNodeFactory.HybridMemoryCmpxchgI16NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNodeFactory.HybridMemoryCmpxchgI32NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNodeFactory.HybridMemoryCmpxchgI64NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryCmpxchgNodeFactory.HybridMemoryCmpxchgI8NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryWriteNode.HybridMemoryWriteI128Node;
import org.graalvm.vm.x86.node.HybridMemoryWriteNode.HybridMemoryWriteI16Node;
import org.graalvm.vm.x86.node.HybridMemoryWriteNode.HybridMemoryWriteI32Node;
import org.graalvm.vm.x86.node.HybridMemoryWriteNode.HybridMemoryWriteI64Node;
import org.graalvm.vm.x86.node.HybridMemoryWriteNode.HybridMemoryWriteI8Node;
import org.graalvm.vm.x86.node.HybridMemoryWriteNodeFactory.HybridMemoryWriteI128NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryWriteNodeFactory.HybridMemoryWriteI16NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryWriteNodeFactory.HybridMemoryWriteI32NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryWriteNodeFactory.HybridMemoryWriteI64NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryWriteNodeFactory.HybridMemoryWriteI8NodeGen;

import sun.misc.Unsafe;

public class MemoryWriteNode extends AMD64Node {
    protected static final boolean MAP_NATIVE_MEMORY = MemoryOptions.MEM_MAP_NATIVE.get();
    protected static final Unsafe unsafe = MAP_NATIVE_MEMORY ? UnsafeHolder.getUnsafe() : null;

    private final VirtualMemory memory;

    @Child private HybridMemoryWriteI8Node writeI8;
    @Child private HybridMemoryWriteI16Node writeI16;
    @Child private HybridMemoryWriteI32Node writeI32;
    @Child private HybridMemoryWriteI64Node writeI64;
    @Child private HybridMemoryWriteI128Node writeI128;
    @Child private HybridMemoryCmpxchgI8Node cmpxchgI8;
    @Child private HybridMemoryCmpxchgI16Node cmpxchgI16;
    @Child private HybridMemoryCmpxchgI32Node cmpxchgI32;
    @Child private HybridMemoryCmpxchgI64Node cmpxchgI64;

    public MemoryWriteNode(VirtualMemory memory) {
        this.memory = memory;
        if (memory instanceof HybridVirtualMemory) {
            HybridVirtualMemory mem = (HybridVirtualMemory) memory;
            NativeVirtualMemory nmem = mem.getNativeVirtualMemory();
            JavaVirtualMemory jmem = mem.getJavaVirtualMemory();
            writeI8 = HybridMemoryWriteI8NodeGen.create(jmem, nmem);
            writeI16 = HybridMemoryWriteI16NodeGen.create(jmem, nmem);
            writeI32 = HybridMemoryWriteI32NodeGen.create(jmem, nmem);
            writeI64 = HybridMemoryWriteI64NodeGen.create(jmem, nmem);
            writeI128 = HybridMemoryWriteI128NodeGen.create(jmem, nmem);
            cmpxchgI8 = HybridMemoryCmpxchgI8NodeGen.create(jmem, nmem);
            cmpxchgI16 = HybridMemoryCmpxchgI16NodeGen.create(jmem, nmem);
            cmpxchgI32 = HybridMemoryCmpxchgI32NodeGen.create(jmem, nmem);
            cmpxchgI64 = HybridMemoryCmpxchgI64NodeGen.create(jmem, nmem);
        }
    }

    public void executeI8(long address, byte value) {
        if (writeI8 != null) {
            writeI8.executeI8(address, value);
        } else {
            if (unsafe != null && address < 0) {
                unsafe.putByte(VirtualMemory.fromMappedNative(address), value);
            } else {
                memory.setI8(address, value);
            }
        }
    }

    public void executeI16(long address, short value) {
        if (writeI16 != null) {
            writeI16.executeI16(address, value);
        } else {
            if (unsafe != null && address < 0) {
                unsafe.putShort(VirtualMemory.fromMappedNative(address), value);
            } else {
                memory.setI16(address, value);
            }
        }
    }

    public void executeI32(long address, int value) {
        if (writeI32 != null) {
            writeI32.executeI32(address, value);
        } else {
            if (unsafe != null && address < 0) {
                unsafe.putInt(VirtualMemory.fromMappedNative(address), value);
            } else {
                memory.setI32(address, value);
            }
        }
    }

    public void executeI64(long address, long value) {
        if (writeI64 != null) {
            writeI64.executeI64(address, value);
        } else {
            if (unsafe != null && address < 0) {
                unsafe.putLong(VirtualMemory.fromMappedNative(address), value);
            } else {
                memory.setI64(address, value);
            }
        }
    }

    public void executeI128(long address, Vector128 value) {
        if (writeI128 != null) {
            writeI128.executeI128(address, value);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                unsafe.putLong(base, value.getI64(1));
                unsafe.putLong(base + 8, value.getI64(0));
            } else {
                memory.setI128(address, value);
            }
        }
    }

    public void executeI256(long address, Vector256 value) {
        if (unsafe != null && address < 0) {
            long base = VirtualMemory.fromMappedNative(address);
            unsafe.putLong(base, value.getI64(3));
            unsafe.putLong(base + 8, value.getI64(2));
            unsafe.putLong(base + 16, value.getI64(1));
            unsafe.putLong(base + 24, value.getI64(0));
        } else {
            memory.setI256(address, value);
        }
    }

    public void executeI512(long address, Vector512 value) {
        if (unsafe != null && address < 0) {
            long base = VirtualMemory.fromMappedNative(address);
            unsafe.putLong(base, value.getI64(7));
            unsafe.putLong(base + 8, value.getI64(6));
            unsafe.putLong(base + 16, value.getI64(5));
            unsafe.putLong(base + 24, value.getI64(4));
            unsafe.putLong(base + 32, value.getI64(3));
            unsafe.putLong(base + 40, value.getI64(2));
            unsafe.putLong(base + 48, value.getI64(1));
            unsafe.putLong(base + 56, value.getI64(0));
        } else {
            memory.setI512(address, value);
        }
    }

    public boolean executeCmpxchgI8(long address, byte expected, byte x) {
        if (cmpxchgI8 != null) {
            return cmpxchgI8.executeI8(address, expected, x);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                return NativeMemory.cmpxchgI8(base, expected, x);
            } else {
                return memory.cmpxchgI8(address, expected, x);
            }
        }
    }

    public boolean executeCmpxchgI16(long address, short expected, short x) {
        if (cmpxchgI16 != null) {
            return cmpxchgI16.executeI16(address, expected, x);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                return NativeMemory.cmpxchgI16L(base, expected, x);
            } else {
                return memory.cmpxchgI16(address, expected, x);
            }
        }
    }

    public boolean executeCmpxchgI32(long address, int expected, int x) {
        if (cmpxchgI32 != null) {
            return cmpxchgI32.executeI32(address, expected, x);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                return NativeMemory.cmpxchgI32L(base, expected, x);
            } else {
                return memory.cmpxchgI32(address, expected, x);
            }
        }
    }

    public boolean executeCmpxchgI64(long address, long expected, long x) {
        if (cmpxchgI64 != null) {
            return cmpxchgI64.executeI64(address, expected, x);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                return NativeMemory.cmpxchgI64L(base, expected, x);
            } else {
                return memory.cmpxchgI64(address, expected, x);
            }
        }
    }
}
