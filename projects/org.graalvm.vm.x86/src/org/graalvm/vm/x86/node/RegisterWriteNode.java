package org.graalvm.vm.x86.node;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class RegisterWriteNode extends AMD64Node {
    private final FrameSlot slot;
    private final int shift;

    public RegisterWriteNode(FrameSlot slot) {
        this.slot = slot;
        this.shift = 0;
    }

    public RegisterWriteNode(FrameSlot slot, int shift) {
        this.slot = slot;
        this.shift = shift;
    }

    public void executeI8(VirtualFrame frame, byte value) {
        long reg = FrameUtil.getLongSafe(frame, slot);
        long val;
        if (shift == 0) {
            val = (reg & ~0xFF) | Byte.toUnsignedLong(value);
        } else {
            val = (reg & ~(0xFF << shift)) | (Byte.toUnsignedLong(value) << shift);
        }
        frame.setLong(slot, val);
    }

    public void executeI16(VirtualFrame frame, short value) {
        long reg = FrameUtil.getLongSafe(frame, slot);
        long val = (reg & ~0xFFFF) | Short.toUnsignedLong(value);
        frame.setLong(slot, val);
    }

    public void executeI32(VirtualFrame frame, int value) {
        frame.setLong(slot, Integer.toUnsignedLong(value));
    }

    public void executeI64(VirtualFrame frame, long value) {
        frame.setLong(slot, value);
    }
}
