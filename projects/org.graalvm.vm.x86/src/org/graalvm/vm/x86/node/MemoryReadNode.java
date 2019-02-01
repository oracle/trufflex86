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
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.util.UnsafeHolder;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI128Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI16Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI32Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI64Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI8Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI128NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI16NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI32NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI64NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI8NodeGen;

import sun.misc.Unsafe;

public class MemoryReadNode extends AMD64Node {
    protected static final boolean MAP_NATIVE_MEMORY = MemoryOptions.MEM_MAP_NATIVE.get();
    protected static final Unsafe unsafe = MAP_NATIVE_MEMORY ? UnsafeHolder.getUnsafe() : null;

    private final VirtualMemory memory;

    @Child private HybridMemoryReadI8Node readI8;
    @Child private HybridMemoryReadI16Node readI16;
    @Child private HybridMemoryReadI32Node readI32;
    @Child private HybridMemoryReadI64Node readI64;
    @Child private HybridMemoryReadI128Node readI128;

    public MemoryReadNode(VirtualMemory memory) {
        this.memory = memory;
        if (memory instanceof HybridVirtualMemory) {
            HybridVirtualMemory mem = (HybridVirtualMemory) memory;
            NativeVirtualMemory nmem = mem.getNativeVirtualMemory();
            JavaVirtualMemory jmem = mem.getJavaVirtualMemory();
            readI8 = HybridMemoryReadI8NodeGen.create(jmem, nmem);
            readI16 = HybridMemoryReadI16NodeGen.create(jmem, nmem);
            readI32 = HybridMemoryReadI32NodeGen.create(jmem, nmem);
            readI64 = HybridMemoryReadI64NodeGen.create(jmem, nmem);
            readI128 = HybridMemoryReadI128NodeGen.create(jmem, nmem);
        }
    }

    public byte executeI8(long address) {
        if (readI8 != null) {
            return readI8.executeI8(address);
        } else {
            if (unsafe != null && address < 0) {
                return unsafe.getByte(VirtualMemory.fromMappedNative(address));
            }
            return memory.getI8(address);
        }
    }

    public short executeI16(long address) {
        if (readI16 != null) {
            return readI16.executeI16(address);
        } else {
            if (unsafe != null && address < 0) {
                return unsafe.getShort(VirtualMemory.fromMappedNative(address));
            }
            return memory.getI16(address);
        }
    }

    public int executeI32(long address) {
        if (readI32 != null) {
            return readI32.executeI32(address);
        } else {
            if (unsafe != null && address < 0) {
                return unsafe.getInt(VirtualMemory.fromMappedNative(address));
            }
            return memory.getI32(address);
        }
    }

    public long executeI64(long address) {
        if (readI64 != null) {
            return readI64.executeI64(address);
        } else {
            if (unsafe != null && address < 0) {
                return unsafe.getLong(VirtualMemory.fromMappedNative(address));
            }
            return memory.getI64(address);
        }
    }

    public Vector128 executeI128(long address) {
        if (readI128 != null) {
            return readI128.executeI128(address);
        } else {
            if (unsafe != null && address < 0) {
                long base = VirtualMemory.fromMappedNative(address);
                long lo = unsafe.getLong(base);
                long hi = unsafe.getLong(base + 8);
                return new Vector128(hi, lo);
            }
            return memory.getI128(address);
        }
    }

    public Vector256 executeI256(long address) {
        if (unsafe != null && address < 0) {
            long base = VirtualMemory.fromMappedNative(address);
            long l1 = unsafe.getLong(base);
            long l2 = unsafe.getLong(base + 8);
            long l3 = unsafe.getLong(base + 16);
            long l4 = unsafe.getLong(base + 24);
            return new Vector256(new long[]{l4, l3, l2, l1});
        }
        return memory.getI256(address);
    }

    public Vector512 executeI512(long address) {
        if (unsafe != null && address < 0) {
            long base = VirtualMemory.fromMappedNative(address);
            long l1 = unsafe.getLong(base);
            long l2 = unsafe.getLong(base + 8);
            long l3 = unsafe.getLong(base + 16);
            long l4 = unsafe.getLong(base + 24);
            long l5 = unsafe.getLong(base + 24);
            long l6 = unsafe.getLong(base + 24);
            long l7 = unsafe.getLong(base + 24);
            long l8 = unsafe.getLong(base + 24);
            return new Vector512(new long[]{l8, l7, l6, l5, l4, l3, l2, l1});
        }
        return memory.getI512(address);
    }
}
