package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ReadFlagNode extends AMD64Node {
    private final FrameSlot slot;

    public ReadFlagNode(FrameSlot slot) {
        this.slot = slot;
    }

    public boolean execute(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, slot);
    }
}
