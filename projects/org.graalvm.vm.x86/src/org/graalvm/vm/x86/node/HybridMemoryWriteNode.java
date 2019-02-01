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

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.util.UnsafeHolder;

import com.oracle.truffle.api.dsl.Specialization;

import sun.misc.Unsafe;

public abstract class HybridMemoryWriteNode extends AMD64Node {
    protected static final boolean MAP_NATIVE_MEMORY = MemoryOptions.MEM_MAP_NATIVE.get();
    protected static final Unsafe unsafe = MAP_NATIVE_MEMORY ? UnsafeHolder.getUnsafe() : null;

    protected final VirtualMemory vmem;
    protected final NativeVirtualMemory nmem;

    public HybridMemoryWriteNode(VirtualMemory vmem, NativeVirtualMemory nmem) {
        this.vmem = vmem;
        this.nmem = nmem;
    }

    public static abstract class HybridMemoryWriteI8Node extends HybridMemoryWriteNode {
        public HybridMemoryWriteI8Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract void executeI8(long address, byte value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected void executeI8MappedNative(long address, byte value) {
            unsafe.putByte(VirtualMemory.fromMappedNative(address), value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected void executeI8Native(long address, byte value) {
            nmem.setI8(address, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected void executeI8Virtual(long address, byte value) {
            vmem.setI8(address, value);
        }
    }

    public static abstract class HybridMemoryWriteI16Node extends HybridMemoryWriteNode {
        public HybridMemoryWriteI16Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract void executeI16(long address, short value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected void executeI16MappedNative(long address, short value) {
            unsafe.putShort(VirtualMemory.fromMappedNative(address), value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected void executeI16Native(long address, short value) {
            nmem.setI16(address, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected void executeI16Virtual(long address, short value) {
            vmem.setI16(address, value);
        }
    }

    public static abstract class HybridMemoryWriteI32Node extends HybridMemoryWriteNode {
        public HybridMemoryWriteI32Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract void executeI32(long address, int value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected void executeI32MappedNative(long address, int value) {
            unsafe.putInt(VirtualMemory.fromMappedNative(address), value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected void executeI32Native(long address, int value) {
            nmem.setI32(address, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected void executeI32Virtual(long address, int value) {
            vmem.setI32(address, value);
        }
    }

    public static abstract class HybridMemoryWriteI64Node extends HybridMemoryWriteNode {
        public HybridMemoryWriteI64Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract void executeI64(long address, long value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected void executeI64MappedNative(long address, long value) {
            unsafe.putLong(VirtualMemory.fromMappedNative(address), value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected void executeI64Native(long address, long value) {
            nmem.setI64(address, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected void executeI64Virtual(long address, long value) {
            vmem.setI64(address, value);
        }
    }

    public static abstract class HybridMemoryWriteI128Node extends HybridMemoryWriteNode {
        public HybridMemoryWriteI128Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract void executeI128(long address, Vector128 value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected void executeI128MappedNative(long address, Vector128 value) {
            long base = VirtualMemory.fromMappedNative(address);
            unsafe.putLong(base, value.getI64(1));
            unsafe.putLong(base + 8, value.getI64(0));
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected void executeI128Native(long address, Vector128 value) {
            nmem.setI128(address, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected void executeI128Virtual(long address, Vector128 value) {
            vmem.setI128(address, value);
        }
    }

    protected boolean isNativeMemory(long address) {
        return Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0;
    }

    protected boolean isMappedNativeMemory(long address) {
        return MAP_NATIVE_MEMORY && address < 0;
    }
}
