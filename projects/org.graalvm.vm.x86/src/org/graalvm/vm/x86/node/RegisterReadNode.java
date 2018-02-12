package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class RegisterReadNode extends ReadNode {
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

    @Override
    public byte executeI8(VirtualFrame frame) {
        return (byte) (FrameUtil.getLongSafe(frame, slot) >> shift);
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        return (short) FrameUtil.getLongSafe(frame, slot);
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        return (int) FrameUtil.getLongSafe(frame, slot);
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, slot);
    }
}
