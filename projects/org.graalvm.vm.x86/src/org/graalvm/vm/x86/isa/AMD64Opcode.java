package org.graalvm.vm.x86.isa;

public class AMD64Opcode {
    public static final byte ESCAPE = 0x0F;

    public static final byte ADD_RM_I = (byte) 0x83;

    public static final byte CALL_REL = (byte) 0xE8;

    public static final byte CMP_RM_I8 = (byte) 0x80;

    public static final byte INC_RM = (byte) 0xFF;

    public static final byte JA = 0x77;
    public static final byte JAE = 0x73;
    public static final byte JB = 0x72;
    public static final byte JBE = 0x76;
    public static final byte JRCXZ = (byte) 0xE3;
    public static final byte JE = 0x74;
    public static final byte JG = 0x7F;
    public static final byte JGE = 0x7D;
    public static final byte JL = 0x7C;
    public static final byte JLE = 0x7E;
    public static final byte JNE = 0x75;
    public static final byte JNO = 0x71;
    public static final byte JNP = 0x7B;
    public static final byte JNS = 0x79;
    public static final byte JO = 0x70;
    public static final byte JP = 0x7A;
    public static final byte JS = 0x78;

    public static final byte JMP = (byte) 0xEB;

    public static final byte LEA = (byte) 0x8D;

    public static final byte LODSB = (byte) 0xAC;
    public static final byte LODSD = (byte) 0xAD;

    public static final byte MOV_RM_R = (byte) 0x89;
    public static final byte MOV_RM_I = (byte) 0xC7;
    public static final byte MOV_R_RM = (byte) 0x8B;
    public static final byte MOV_R_I = (byte) 0xB8;

    public static final byte MOVSXD_R_RM = 0x63;

    public static final byte NOP = (byte) 0x90;
    public static final byte NOP_RM = 0x1f;

    public static final byte POP_R = 0x58;
    public static final byte PUSH_R = 0x50;

    public static final byte RET_FAR = (byte) 0xCB;
    public static final byte RET_NEAR = (byte) 0xC3;

    public static final byte SUB_RM_R = 0x29;
    public static final byte SUB_RM_I = (byte) 0x83;

    public static final byte TEST_RM_R8 = (byte) 0x84;
    public static final byte TEST_RM_R = (byte) 0x85;

    public static final byte XOR_RM8_R8 = 0x30;
    public static final byte XOR_RM_R = 0x31;
    public static final byte XOR_R8_RM8 = 0x32;
    public static final byte XOR_R_RM = 0x33;

    // PREFIX: 0x0F (ESCAPE)
    public static final byte SYSCALL = 0x05;
}
