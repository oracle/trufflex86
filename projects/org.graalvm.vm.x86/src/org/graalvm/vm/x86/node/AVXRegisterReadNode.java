package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AVXRegisterReadNode extends ReadNode {
    private final FrameSlot slot;

    public AVXRegisterReadNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public byte executeI8(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return (byte) reg.getI32(15);
    }

    @Override
    public short executeI16(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return (short) reg.getI32(15);
    }

    @Override
    public int executeI32(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return reg.getI32(15);
    }

    @Override
    public long executeI64(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return reg.getI64(7);
    }

    @Override
    public Vector128 executeI128(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return reg.getI128(3);
    }

    @Override
    public Vector256 executeI256(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return reg.getI256(1);
    }

    @Override
    public Vector512 executeI512(VirtualFrame frame) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        return reg;
    }
}
