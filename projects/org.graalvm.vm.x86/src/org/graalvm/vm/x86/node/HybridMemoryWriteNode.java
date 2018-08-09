package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class HybridMemoryWriteNode extends AMD64Node {
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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
}
