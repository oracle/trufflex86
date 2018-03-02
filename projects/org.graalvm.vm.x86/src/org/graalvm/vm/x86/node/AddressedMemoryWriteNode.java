package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AddressedMemoryWriteNode extends WriteNode {
    @Child private AddressComputationNode address;
    @Child private MemoryWriteNode writeMemory;

    public AddressedMemoryWriteNode(AddressComputationNode address, MemoryWriteNode writeMemory) {
        this.address = address;
        this.writeMemory = writeMemory;
    }

    @Override
    public void executeI8(VirtualFrame frame, byte value) {
        long addr = address.execute(frame);
        writeMemory.executeI8(addr, value);
    }

    @Override
    public void executeI16(VirtualFrame frame, short value) {
        long addr = address.execute(frame);
        writeMemory.executeI16(addr, value);
    }

    @Override
    public void executeI32(VirtualFrame frame, int value) {
        long addr = address.execute(frame);
        writeMemory.executeI32(addr, value);
    }

    @Override
    public void executeI64(VirtualFrame frame, long value) {
        long addr = address.execute(frame);
        writeMemory.executeI64(addr, value);
    }

    @Override
    public void executeI128(VirtualFrame frame, Vector128 value) {
        long addr = address.execute(frame);
        writeMemory.executeI128(addr, value);
    }

    @Override
    public void executeI256(VirtualFrame frame, Vector256 value) {
        long addr = address.execute(frame);
        writeMemory.executeI256(addr, value);
    }

    @Override
    public void executeI512(VirtualFrame frame, Vector512 value) {
        long addr = address.execute(frame);
        writeMemory.executeI512(addr, value);
    }
}
