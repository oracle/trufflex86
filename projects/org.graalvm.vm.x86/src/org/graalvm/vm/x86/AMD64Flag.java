package org.graalvm.vm.x86;

import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class AMD64Flag {
    private final FrameSlot slot;

    public AMD64Flag(FrameSlot slot) {
        this.slot = slot;
    }

    public ReadFlagNode createRead() {
        return new ReadFlagNode(slot);
    }

    public WriteFlagNode createWrite() {
        return new WriteFlagNode(slot);
    }
}
