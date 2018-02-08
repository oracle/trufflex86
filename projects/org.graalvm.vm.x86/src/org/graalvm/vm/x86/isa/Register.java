package org.graalvm.vm.x86.isa;

public enum Register {
    AL(0),
    AH(0),
    CL(1),
    CH(1),
    DL(2),
    DH(2),
    BL(3),
    BH(3),
    AX(0),
    CX(1),
    DX(2),
    BX(3),
    SP(4),
    BP(5),
    SI(6),
    DI(7),
    EAX(0),
    ECX(1),
    EDX(2),
    EBX(3),
    ESP(4),
    EBP(5),
    ESI(6),
    EDI(7),
    RAX(0),
    RCX(1),
    RDX(2),
    RBX(3),
    RSP(4),
    RBP(5),
    RSI(6),
    RDI(7),
    R8(8),
    R9(9),
    R10(10),
    R11(11),
    R12(12),
    R13(13),
    R14(14),
    R15(15);

    private Register(int id) {
        this.id = id;
    }

    private final int id;

    public static Register[] REGISTERS = {
                    RAX, RCX, RDX, RBX, RSP, RBP, RSI, RDI,
                    R8, R9, R10, R11, R12, R13, R14, R15
    };

    static {
        for (int i = 0; i < REGISTERS.length; i++) {
            assert REGISTERS[i].getID() == i;
        }
    }

    public int getID() {
        return id;
    }

    public Register get64bit() {
        switch (this) {
            case AX:
            case EAX:
                return RAX;
            case BX:
            case EBX:
                return RBX;
            case CX:
            case ECX:
                return RCX;
            case DX:
            case EDX:
                return RDX;
            case SI:
            case ESI:
                return RSI;
            case DI:
            case EDI:
                return RDI;
            case BP:
            case EBP:
                return RBP;
            case SP:
            case ESP:
                return RSP;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Register get(int id) {
        return REGISTERS[id];
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
