package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public class WriteFlagNode extends AMD64Node {
    private final FrameSlot slot;

    public WriteFlagNode(FrameSlot slot) {
        this.slot = slot;
    }

    public void execute(VirtualFrame frame, boolean value) {
        frame.setBoolean(slot, value);
    }
}
