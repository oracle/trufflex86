package org.graalvm.vm.x86;

import org.graalvm.vm.x86.isa.Register;

import com.oracle.truffle.api.frame.FrameSlot;

public class RegisterAccessFactory {
    private final FrameSlot[] gpr;
    private final FrameSlot pc;

    public RegisterAccessFactory(FrameSlot[] gpr, FrameSlot pc) {
        this.gpr = gpr;
        this.pc = pc;
    }

    public AMD64Register getRegister(Register reg) {
        switch (reg) {
            case AH:
            case BH:
            case CH:
            case DH:
                return new AMD64Register(gpr[reg.getID()], 8);
            default:
                return new AMD64Register(gpr[reg.getID()]);
        }
    }

    public AMD64Register getPC() {
        return new AMD64Register(pc);
    }
}
