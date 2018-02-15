package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AddressedMemoryReadNode extends ReadNode {
    @Child private AddressComputationNode address;
    @Child private MemoryReadNode readMemory;

    public AddressedMemoryReadNode(AddressComputationNode address, MemoryReadNode readMemory) {
        this.address = address;
        this.readMemory = readMemory;
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI8(addr);
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI16(addr);
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI32(addr);
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI64(addr);
    }

    @Override
    public Vector128 executeI128(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI128(addr);
    }

    @Override
    public Vector256 executeI256(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI256(addr);
    }

    @Override
    public Vector512 executeI512(VirtualFrame frame) {
        long addr = address.execute(frame);
        return readMemory.executeI512(addr);
    }
}
