package org.graalvm.vm.x86.isa;

public class AMD64Opcode {
    public static final byte ESCAPE = 0x0F;

    public static final byte INC_RM = (byte) 0xFF;

    public static final byte MOV_RM_R = (byte) 0x89;
    public static final byte MOV_RM_I = (byte) 0xC7;

    public static final byte MOV_R_I = (byte) 0xB8;

    public static final byte XOR_RM8_R8 = 0x30;
    public static final byte XOR_RM_R = 0x31;
    public static final byte XOR_R8_RM8 = 0x32;
    public static final byte XOR_R_RM = 0x33;

    // PREFIX: 0x0F (ESCAPE)
    public static final byte SYSCALL = 0x05;
}
