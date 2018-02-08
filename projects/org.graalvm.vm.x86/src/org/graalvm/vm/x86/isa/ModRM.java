package org.graalvm.vm.x86.isa;

public class ModRM {
    public static final int A8 = 0;
    public static final int A16 = 1;
    public static final int A32 = 2;
    public static final int A64 = 3;

    public static final int R8 = 0;
    public static final int R16 = 1;
    public static final int R32 = 2;
    public static final int R64 = 3;

    private final byte modrm;

    private final int mod;
    private final int reg;
    private final int rm;

    private static final Operand[][] OP1_R = {
                    // R8
                    {
                                    new RegisterOperand(Register.AL),
                                    new RegisterOperand(Register.CL),
                                    new RegisterOperand(Register.DL),
                                    new RegisterOperand(Register.BL),
                                    new RegisterOperand(Register.AH),
                                    new RegisterOperand(Register.CH),
                                    new RegisterOperand(Register.DH),
                                    new RegisterOperand(Register.BH)
                    },
                    // R16
                    {
                                    new RegisterOperand(Register.AX),
                                    new RegisterOperand(Register.CX),
                                    new RegisterOperand(Register.DX),
                                    new RegisterOperand(Register.BX),
                                    new RegisterOperand(Register.SP),
                                    new RegisterOperand(Register.BP),
                                    new RegisterOperand(Register.SI),
                                    new RegisterOperand(Register.DI)
                    },
                    // R32
                    {
                                    new RegisterOperand(Register.EAX),
                                    new RegisterOperand(Register.ECX),
                                    new RegisterOperand(Register.EDX),
                                    new RegisterOperand(Register.EBX),
                                    new RegisterOperand(Register.ESP),
                                    new RegisterOperand(Register.EBP),
                                    new RegisterOperand(Register.ESI),
                                    new RegisterOperand(Register.EDI)
                    },
                    // R64
                    {
                                    new RegisterOperand(Register.RAX),
                                    new RegisterOperand(Register.RCX),
                                    new RegisterOperand(Register.RDX),
                                    new RegisterOperand(Register.RBX),
                                    new RegisterOperand(Register.RSP),
                                    new RegisterOperand(Register.RBP),
                                    new RegisterOperand(Register.RSI),
                                    new RegisterOperand(Register.RDI)
                    }
    };

    private static final Operand[][][] OP1 = {
                    // R8
                    {},
                    // R16
                    {},
                    // R32
                    {
                                    // mod=0
                                    {
                                                    new MemoryOperand(Register.EAX),
                                                    new MemoryOperand(Register.ECX),
                                                    new MemoryOperand(Register.EDX),
                                                    new MemoryOperand(Register.EBX),
                                                    null, // SIB
                                                    null, // disp32
                                                    new MemoryOperand(Register.ESI),
                                                    new MemoryOperand(Register.EDI)
                                    },
                                    // mod=1
                                    {},
                                    // mod=2
                                    {},
                                    // mod=3
                                    {
                                                    new RegisterOperand(Register.EAX),
                                                    new RegisterOperand(Register.ECX),
                                                    new RegisterOperand(Register.EDX),
                                                    new RegisterOperand(Register.EBX),
                                                    new RegisterOperand(Register.ESP),
                                                    new RegisterOperand(Register.EBP),
                                                    new RegisterOperand(Register.ESI),
                                                    new RegisterOperand(Register.EDI),
                                    }
                    }
    };

    private static final Register[][] OP2 = {
                    // R8
                    {Register.AL, Register.CL, Register.DL, Register.BL, Register.AH, Register.CH, Register.DH, Register.BH},
                    // R16
                    {Register.AX, Register.CX, Register.DX, Register.BX, Register.SP, Register.BP, Register.SI, Register.DI},
                    // R32
                    {Register.EAX, Register.ECX, Register.EDX, Register.EBX, Register.ESP, Register.EBP, Register.ESI, Register.EDI},
    };

    public ModRM(byte modrm) {
        this.modrm = modrm;
        mod = (modrm >> 6) & 0x03;
        reg = (modrm >> 3) & 0x07;
        rm = modrm & 0x07;
    }

    public byte getModRM() {
        return modrm;
    }

    public Operand getOperand1(int type, int size) {
        if (mod == 0b11) {
            return OP1_R[size][rm];
        }
        switch (type) {
            case A8:
            case A16:
            case A32:
                return OP1[type][mod][rm];
            default:
                throw new IllegalArgumentException();
        }
    }

    public Register getOperand2(int type) {
        return OP2[type][reg];
    }

    public boolean hasSIB() {
        return mod != 0b11 && rm == 0b100;
    }

    public int getDisplacementSize() {
        switch (mod) {
            case 0:
                return rm == 0b101 ? 4 : 0;
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
            default:
                return 0;
        }
    }

    public boolean hasDisplacement() {
        return getDisplacementSize() > 0;
    }

    @Override
    public String toString() {
        return "ModRM[mod=" + mod + ",reg=" + reg + ",rm=" + rm + "]";
    }
}
