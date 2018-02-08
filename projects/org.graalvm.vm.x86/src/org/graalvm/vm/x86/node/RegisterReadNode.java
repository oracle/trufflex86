package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class RegisterReadNode extends AMD64Node {
    private final FrameSlot slot;
    private final int shift;

    public RegisterReadNode(FrameSlot slot) {
        this.slot = slot;
        this.shift = 0;
    }

    public RegisterReadNode(FrameSlot slot, int shift) {
        this.slot = slot;
        this.shift = shift;
    }

    public byte executeI8(VirtualFrame frame) {
        return (byte) (FrameUtil.getLongSafe(frame, slot) >> shift);
    }

    public short executeI16(VirtualFrame frame) {
        return (short) FrameUtil.getLongSafe(frame, slot);
    }

    public int executeI32(VirtualFrame frame) {
        return (int) FrameUtil.getLongSafe(frame, slot);
    }

    public long executeI64(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, slot);
    }
}
