package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;

import com.everyware.util.UnsafeHolder;
import com.oracle.truffle.api.dsl.Specialization;

import sun.misc.Unsafe;

public abstract class HybridMemoryReadNode extends AMD64Node {
    protected static final boolean MAP_NATIVE_MEMORY = MemoryOptions.MEM_MAP_NATIVE.get();
    protected static final Unsafe unsafe = MAP_NATIVE_MEMORY ? UnsafeHolder.getUnsafe() : null;

    protected final VirtualMemory vmem;
    protected final NativeVirtualMemory nmem;

    public HybridMemoryReadNode(VirtualMemory vmem, NativeVirtualMemory nmem) {
        this.vmem = vmem;
        this.nmem = nmem;
    }

    public static abstract class HybridMemoryReadI8Node extends HybridMemoryReadNode {
        public HybridMemoryReadI8Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract byte executeI8(long address);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected byte executeI8MappedNative(long address) {
            return unsafe.getByte(VirtualMemory.fromMappedNative(address));
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected byte executeI8Native(long address) {
            return nmem.getI8(address);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected byte executeI8Virtual(long address) {
            return vmem.getI8(address);
        }
    }

    public static abstract class HybridMemoryReadI16Node extends HybridMemoryReadNode {
        public HybridMemoryReadI16Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract short executeI16(long address);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected short executeI16MappedNative(long address) {
            return unsafe.getShort(VirtualMemory.fromMappedNative(address));
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected short executeI16Native(long address) {
            return nmem.getI16(address);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected short executeI16Virtual(long address) {
            return vmem.getI16(address);
        }
    }

    public static abstract class HybridMemoryReadI32Node extends HybridMemoryReadNode {
        public HybridMemoryReadI32Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract int executeI32(long address);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected int executeI32MappedNative(long address) {
            return unsafe.getInt(VirtualMemory.fromMappedNative(address));
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected int executeI32Native(long address) {
            return nmem.getI32(address);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected int executeI32Virtual(long address) {
            return vmem.getI32(address);
        }
    }

    public static abstract class HybridMemoryReadI64Node extends HybridMemoryReadNode {
        public HybridMemoryReadI64Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract long executeI64(long address);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected long executeI64MappedNative(long address) {
            return unsafe.getLong(VirtualMemory.fromMappedNative(address));
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected long executeI64Native(long address) {
            return nmem.getI64(address);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected long executeI64Virtual(long address) {
            return vmem.getI64(address);
        }
    }

    public static abstract class HybridMemoryReadI128Node extends HybridMemoryReadNode {
        public HybridMemoryReadI128Node(VirtualMemory vmem, NativeVirtualMemory nmem) {
            super(vmem, nmem);
        }

        public abstract Vector128 executeI128(long address);

        @Specialization(guards = {"isMappedNativeMemory(address)"})
        protected Vector128 executeI128MappedNative(long address) {
            long base = VirtualMemory.fromMappedNative(address);
            long lo = unsafe.getLong(base);
            long hi = unsafe.getLong(base + 8);
            return new Vector128(hi, lo);
        }

        @Specialization(guards = {"!isMappedNativeMemory(address)", "isNativeMemory(address)"})
        protected Vector128 executeI128Native(long address) {
            return nmem.getI128(address);
        }

        @Specialization(guards = {"!isNativeMemory(address)"})
        protected Vector128 executeI128Virtual(long address) {
            return vmem.getI128(address);
        }
    }

    protected boolean isNativeMemory(long address) {
        return Long.compareUnsigned(address, nmem.getVirtualHigh()) < 0;
    }

    protected boolean isMappedNativeMemory(long address) {
        return MAP_NATIVE_MEMORY && address < 0;
    }
}
