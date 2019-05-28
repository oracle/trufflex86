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
import org.graalvm.vm.memory.hardware.NativeMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.util.UnsafeHolder;

import com.oracle.truffle.api.dsl.Specialization;

import sun.misc.Unsafe;

public abstract class HybridMemoryCmpxchgNode extends AMD64Node {
    protected static final boolean MAP_NATIVE_MEMORY = MemoryOptions.MEM_MAP_NATIVE.get();
    protected static final Unsafe unsafe = MAP_NATIVE_MEMORY ? UnsafeHolder.getUnsafe() : null;

    protected final VirtualMemory vmem;
    protected final NativeVirtualMemory nmem;

    public HybridMemoryCmpxchgNode(VirtualMemory vmem, NativeVirtualMemory nmem) {
        this.vmem = vmem;
        this.nmem = nmem;
    }

    public static abstract class HybridMemoryCmpxchgI8Node extends HybridMemoryCmpxchgNode {
        public HybridMemoryCmpxchgI8Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract boolean executeI8(long address, byte expected, byte value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected boolean executeI8MappedNative(long address, byte expected, byte value) {
            return NativeMemory.cmpxchgI8(VirtualMemory.fromMappedNative(address), expected, value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected boolean executeI8Native(long address, byte expected, byte value) {
            return nmem.cmpxchgI8(address, expected, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected boolean executeI8Virtual(long address, byte expected, byte value) {
            return vmem.cmpxchgI8(address, expected, value);
        }
    }

    public static abstract class HybridMemoryCmpxchgI16Node extends HybridMemoryCmpxchgNode {
        public HybridMemoryCmpxchgI16Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract boolean executeI16(long address, short expected, short value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected boolean executeI16MappedNative(long address, short expected, short value) {
            return NativeMemory.cmpxchgI16L(VirtualMemory.fromMappedNative(address), expected, value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected boolean executeI16Native(long address, short expected, short value) {
            return nmem.cmpxchgI16(address, expected, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected boolean executeI16Virtual(long address, short expected, short value) {
            return vmem.cmpxchgI16(address, expected, value);
        }
    }

    public static abstract class HybridMemoryCmpxchgI32Node extends HybridMemoryCmpxchgNode {
        public HybridMemoryCmpxchgI32Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract boolean executeI32(long address, int expected, int value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected boolean executeI32MappedNative(long address, int expected, int value) {
            return NativeMemory.cmpxchgI32L(VirtualMemory.fromMappedNative(address), expected, value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected boolean executeI32Native(long address, int expected, int value) {
            return nmem.cmpxchgI32(address, expected, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected boolean executeI32Virtual(long address, int expected, int value) {
            return vmem.cmpxchgI32(address, expected, value);
        }
    }

    public static abstract class HybridMemoryCmpxchgI64Node extends HybridMemoryCmpxchgNode {
        public HybridMemoryCmpxchgI64Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract boolean executeI64(long address, long expected, long value);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected boolean executeI64MappedNative(long address, long expected, long value) {
            return NativeMemory.cmpxchgI64L(VirtualMemory.fromMappedNative(address), expected, value);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected boolean executeI64Native(long address, long expected, long value) {
            return nmem.cmpxchgI64(address, expected, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected boolean executeI64Virtual(long address, long expected, long value) {
            return vmem.cmpxchgI64(address, expected, value);
        }
    }

    public static abstract class HybridMemoryCmpxchgI128Node extends HybridMemoryCmpxchgNode {
        public HybridMemoryCmpxchgI128Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract boolean executeI128(long address, Vector128 expected, Vector128 value);

        @SuppressWarnings("unused")
        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected boolean executeI128MappedNative(long address, Vector128 expected, Vector128 value) {
            throw new UnsupportedOperationException();
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected boolean executeI128Native(long address, Vector128 expected, Vector128 value) {
            return nmem.cmpxchgI128(address, expected, value);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected boolean executeI128Virtual(long address, Vector128 expected, Vector128 value) {
            return vmem.cmpxchgI128(address, expected, value);
        }
    }

    protected boolean isNativeMemory(long address) {
        return Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0;
    }

    protected boolean isMappedNativeMemory(long address) {
        return MAP_NATIVE_MEMORY && address < 0;
    }
}
