package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.node.AVXRegisterReadNode;
import org.graalvm.vm.x86.node.AVXRegisterWriteNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class AVXRegister {
    public static final int TYPE_ZMM = 0;
    public static final int TYPE_XMM = 1;
    public static final int TYPE_F32 = 2;
    public static final int TYPE_F64 = 3;

    private final FrameSlot zmm;
    private final FrameSlot xmm;
    private final FrameSlot xmmF32;
    private final FrameSlot xmmF64;
    private final FrameSlot xmmType;

    public AVXRegister(FrameSlot zmm, FrameSlot xmm, FrameSlot xmmF32, FrameSlot xmmF64, FrameSlot xmmType) {
        this.zmm = zmm;
        this.xmm = xmm;
        this.xmmF32 = xmmF32;
        this.xmmF64 = xmmF64;
        this.xmmType = xmmType;
    }

    public AVXRegisterReadNode createRead() {
        return new AVXRegisterReadNode(zmm, xmm, xmmF32, xmmF64, xmmType);
    }

    public AVXRegisterWriteNode createWrite() {
        return new AVXRegisterWriteNode(zmm, xmm, xmmF32, xmmF64, xmmType);
    }

    @Override
    public String toString() {
        return zmm.getIdentifier().toString();
    }
}
