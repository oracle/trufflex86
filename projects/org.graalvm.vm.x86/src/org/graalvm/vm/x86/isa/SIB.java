package org.graalvm.vm.x86.isa;

public class SIB {
    private final byte sib;
    private final int ss;
    private final int index;
    private final int base;

    private static final Register[] INDEX = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, null, Register.EBP, Register.ESI, Register.EDI};
    private static final Register[] BASE = {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, null, Register.ESI, Register.EDI};

    public SIB(byte sib) {
        this.sib = sib;
        ss = (sib >> 6) & 0x03;
        index = (sib >> 3) & 0x07;
        base = sib & 0x07;
    }

    public byte getSIB() {
        return sib;
    }

    public int getShift() {
        return ss;
    }

    public Register getIndex() {
        return INDEX[index];
    }

    public Register getBase() {
        return BASE[base];
    }

    public Register getBase(boolean ext) {
        return Register.get(base + (ext ? 8 : 0));
    }

    public Register getIndex(boolean ext) {
        return Register.get(index + (ext ? 8 : 0));
    }
}
