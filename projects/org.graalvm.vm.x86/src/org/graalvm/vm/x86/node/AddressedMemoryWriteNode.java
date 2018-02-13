package org.graalvm.vm.x86.node;

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
}
