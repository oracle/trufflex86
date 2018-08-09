package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class HybridMemoryReadNode extends AMD64Node {
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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

        @Specialization(guards = {"isNativeMemory(address)"})
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
}
