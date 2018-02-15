package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.node.AVXRegisterReadNode;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class AVXRegister {
    private final FrameSlot slot;

    public AVXRegister(FrameSlot slot) {
        this.slot = slot;
    }

    public AVXRegisterReadNode createRead() {
        return new AVXRegisterReadNode(slot);
    }

    public AVXRegisterWriteNode createWrite() {
        return new AVXRegisterWriteNode(slot);
    }

    @Override
    public String toString() {
        return slot.getIdentifier().toString();
    }
}
