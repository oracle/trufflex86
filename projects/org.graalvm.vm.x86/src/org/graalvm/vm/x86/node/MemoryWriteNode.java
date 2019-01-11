package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.HybridVirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
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

import com.everyware.util.UnsafeHolder;

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
}
