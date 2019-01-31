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
