package org.graalvm.vm.x86;

import org.graalvm.vm.x86.isa.AVXRegister;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadFlagsNode;

import com.oracle.truffle.api.frame.FrameSlot;

public class RegisterAccessFactory {
    private final FrameSlot[] gpr;
    private final FrameSlot[] zmm;
    private final FrameSlot pc;

    private final FrameSlot cf;
    private final FrameSlot pf;
    private final FrameSlot af;
    private final FrameSlot zf;
    private final FrameSlot sf;
    private final FrameSlot df;
    private final FrameSlot of;

    public RegisterAccessFactory(FrameSlot[] gpr, FrameSlot[] zmm, FrameSlot pc, FrameSlot cf, FrameSlot pf, FrameSlot af, FrameSlot zf, FrameSlot sf, FrameSlot df, FrameSlot of) {
        this.gpr = gpr;
        this.zmm = zmm;
        this.pc = pc;
        this.cf = cf;
        this.pf = pf;
        this.af = af;
        this.zf = zf;
        this.sf = sf;
        this.df = df;
        this.of = of;
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

    public AVXRegister getAVXRegister(int i) {
        return new AVXRegister(zmm[i]);
    }

    public AMD64Register getPC() {
        return new AMD64Register(pc);
    }

    public AMD64Flag getCF() {
        return new AMD64Flag(cf);
    }

    public AMD64Flag getPF() {
        return new AMD64Flag(pf);
    }

    public AMD64Flag getAF() {
        return new AMD64Flag(af);
    }

    public AMD64Flag getZF() {
        return new AMD64Flag(zf);
    }

    public AMD64Flag getSF() {
        return new AMD64Flag(sf);
    }

    public AMD64Flag getDF() {
        return new AMD64Flag(df);
    }

    public AMD64Flag getOF() {
        return new AMD64Flag(of);
    }

    public ReadFlagsNode createReadFlags() {
        return new ReadFlagsNode();
    }
}
