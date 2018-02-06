package org.graalvm.vm.x86.isa;

public enum Register {
    AL,
    AH,
    BL,
    BH,
    CL,
    CH,
    DL,
    DH,
    AX,
    BX,
    CX,
    DX,
    BP,
    SP,
    SI,
    DI,
    EAX,
    EBX,
    ECX,
    EDX,
    EBP,
    ESP,
    ESI,
    EDI,
    RAX,
    RBX,
    RCX,
    RDX,
    RBP,
    RSP,
    RSI,
    RDI,
    R8,
    R9,
    R10,
    R11,
    R12,
    R13,
    R14,
    R15;

    public static Register[] REGISTERS = {
                    RAX, RCX, RDX, RBX, RSP, RBP, RSI, RDI,
                    R8, R9, R10, R11, R12, R13, R14, R15
    };

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
