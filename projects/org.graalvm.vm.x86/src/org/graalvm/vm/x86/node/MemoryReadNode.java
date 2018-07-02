package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.hardware.HybridVirtualMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI16Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI32Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI64Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNode.HybridMemoryReadI8Node;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI16NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI32NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI64NodeGen;
import org.graalvm.vm.x86.node.HybridMemoryReadNodeFactory.HybridMemoryReadI8NodeGen;

public class MemoryReadNode extends AMD64Node {
    private final VirtualMemory memory;

    @Child private HybridMemoryReadI8Node readI8;
    @Child private HybridMemoryReadI16Node readI16;
    @Child private HybridMemoryReadI32Node readI32;
    @Child private HybridMemoryReadI64Node readI64;

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
        }
    }

    public byte executeI8(long address) {
        if (readI8 != null) {
            return readI8.executeI8(address);
        } else {
            return memory.getI8(address);
        }
    }

    public short executeI16(long address) {
        if (readI16 != null) {
            return readI16.executeI16(address);
        } else {
            return memory.getI16(address);
        }
    }

    public int executeI32(long address) {
        if (readI32 != null) {
            return readI32.executeI32(address);
        } else {
            return memory.getI32(address);
        }
    }

    public long executeI64(long address) {
        if (readI64 != null) {
            return readI64.executeI64(address);
        } else {
            return memory.getI64(address);
        }
    }

    public Vector128 executeI128(long address) {
        return memory.getI128(address);
    }

    public Vector256 executeI256(long address) {
        return memory.getI256(address);
    }

    public Vector512 executeI512(long address) {
        return memory.getI512(address);
    }
}
