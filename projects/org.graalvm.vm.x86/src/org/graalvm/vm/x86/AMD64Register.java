package org.graalvm.vm.x86;

import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class AMD64Register {
    private final FrameSlot slot;
    private final int shift;

    public AMD64Register(FrameSlot slot) {
        this(slot, 0);
    }

    public AMD64Register(FrameSlot slot, int shift) {
        this.slot = slot;
        this.shift = shift;
    }

    public RegisterReadNode createRead() {
        return new RegisterReadNode(slot, shift);
    }

    public RegisterWriteNode createWrite() {
        return new RegisterWriteNode(slot, shift);
    }

    public FrameSlot getSlot() {
        return slot;
    }
}
