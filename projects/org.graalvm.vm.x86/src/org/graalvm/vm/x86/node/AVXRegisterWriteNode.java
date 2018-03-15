package org.graalvm.vm.x86.node;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AVXRegisterWriteNode extends WriteNode {
    private final FrameSlot slot;

    public AVXRegisterWriteNode(FrameSlot slot) {
        this.slot = slot;
    }

    public void executeI32(VirtualFrame frame, int i, int value) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        reg.setI32(i, value);
    }

    public void executeI64(VirtualFrame frame, int i, long value) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        reg.setI64(i, value);
    }

    public void executeI128(VirtualFrame frame, int i, Vector128 value) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        reg.setI128(i, value);
    }

    public void executeI256(VirtualFrame frame, int i, Vector256 value) {
        Vector512 reg = (Vector512) FrameUtil.getObjectSafe(frame, slot);
        reg.setI256(i, value);
    }

    @Override
    public void executeI512(VirtualFrame frame, Vector512 value) {
        frame.setObject(slot, value.clone());
    }

    @Override
    public void executeI8(VirtualFrame frame, byte value) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeI16(VirtualFrame frame, short value) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeI32(VirtualFrame frame, int value) {
        executeI32(frame, 15, value);
    }

    @Override
    public void executeI64(VirtualFrame frame, long value) {
        executeI64(frame, 7, value);
    }

    @Override
    public void executeI128(VirtualFrame frame, Vector128 value) {
        executeI128(frame, 3, value);
    }

    @Override
    public void executeI256(VirtualFrame frame, Vector256 value) {
        executeI256(frame, 1, value);
    }
}
