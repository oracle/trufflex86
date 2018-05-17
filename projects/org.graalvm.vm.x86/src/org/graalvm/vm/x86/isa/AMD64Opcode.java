package org.graalvm.vm.x86.isa;

public class AMD64Opcode {
    public static final byte ESCAPE = 0x0F;
    public static final byte FENCE = (byte) 0xAE;

    public static final byte ADC_RM_R = 0x11;
    public static final byte ADC_R_RM = 0x13;

    public static final byte ADD_A_I = 0x05;
    public static final byte ADD_RM8_R = 0x00;
    public static final byte ADD_RM_R = 0x01;
    public static final byte ADD_R_RM = 0x03;
    public static final byte ADD_RM_I8 = (byte) 0x83;

    public static final byte ADDSD_X_XM = 0x58;

    public static final byte AND_A_I8 = 0x24;
    public static final byte AND_A_I = 0x25;
    public static final byte AND_RM_I8 = (byte) 0x83;
    public static final byte AND_RM8_R8 = 0x20;
    public static final byte AND_RM_R = 0x21;
    public static final byte AND_R8_RM8 = 0x22;
    public static final byte AND_R_RM = 0x23;

    public static final byte ANDNPD_X_XM = 0x55;

    public static final byte ANDPD_X_XM = 0x54;

    public static final byte BSF_R_RM = (byte) 0xBC;

    public static final byte BSR_R_RM = (byte) 0xBD;

    public static final byte BSWAP = (byte) 0xC8;

    public static final byte BT_RM_R = (byte) 0xA3;
    public static final byte BTS_RM_R = (byte) 0xAB;

    public static final byte CALL_REL = (byte) 0xE8;
    public static final byte CALL_RM = (byte) 0xFF;

    public static final byte CDQE = (byte) 0x98;
    public static final byte CDQ = (byte) 0x99;

    public static final byte CLD = (byte) 0xFC;

    public static final byte CMOVA = 0x47;
    public static final byte CMOVAE = 0x43;
    public static final byte CMOVB = 0x42;
    public static final byte CMOVBE = 0x46;
    public static final byte CMOVE = 0x44;
    public static final byte CMOVG = 0x4F;
    public static final byte CMOVGE = 0x4D;
    public static final byte CMOVL = 0x4C;
    public static final byte CMOVLE = 0x4E;
    public static final byte CMOVNE = 0x45;
    public static final byte CMOVNO = 0x41;
    public static final byte CMOVNP = 0x4B;
    public static final byte CMOVNS = 0x49;
    public static final byte CMOVO = 0x40;
    public static final byte CMOVP = 0x4A;
    public static final byte CMOVS = 0x48;

    public static final byte CMP_AL_I = 0x3C;
    public static final byte CMP_A_I = 0x3D;
    public static final byte CMP_RM_I8 = (byte) 0x80;
    public static final byte CMP_RM_I = (byte) 0x81;
    public static final byte CMP_RM_R8 = 0x38;
    public static final byte CMP_RM_R = 0x39;
    public static final byte CMP_R8_RM8 = 0x3A;
    public static final byte CMP_R_RM = 0x3B;

    public static final byte CMPPD_X_XM = (byte) 0xC2;

    public static final byte CMPSB = (byte) 0xA6;
    public static final byte CMPSD = (byte) 0xA7;

    public static final byte CMPXCHG_RM_R = (byte) 0xB1;

    public static final byte CPUID = (byte) 0xA2;

    public static final byte CVTDQ2PD = (byte) 0xE6;

    public static final byte CVTDQ2PS = (byte) 0x5B;

    public static final byte CVTSI2SD = 0x2A;

    public static final byte CVTSS2SD = 0x5A;

    public static final byte CVTTSD2SI = 0x2C;

    public static final byte DIVSD_X_XM = 0x5E;

    public static final byte FNSTCW_M = (byte) 0xD9;

    public static final byte IDIV_RM8 = (byte) 0xF6;
    public static final byte IDIV_RM = (byte) 0xF7;

    public static final byte IMUL_R_RM = (byte) 0xAF;
    public static final byte IMUL_R_RM_I8 = 0x6B;
    public static final byte IMUL_R_RM_I = 0x69;

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

    public static final byte JA32 = (byte) 0x87;
    public static final byte JAE32 = (byte) 0x83;
    public static final byte JB32 = (byte) 0x82;
    public static final byte JBE32 = (byte) 0x86;
    public static final byte JE32 = (byte) 0x84;
    public static final byte JG32 = (byte) 0x8F;
    public static final byte JGE32 = (byte) 0x8D;
    public static final byte JL32 = (byte) 0x8C;
    public static final byte JLE32 = (byte) 0x8E;
    public static final byte JNE32 = (byte) 0x85;
    public static final byte JNO32 = (byte) 0x81;
    public static final byte JNP32 = (byte) 0x8B;
    public static final byte JNS32 = (byte) 0x89;
    public static final byte JO32 = (byte) 0x80;
    public static final byte JP32 = (byte) 0x8A;
    public static final byte JS32 = (byte) 0x88;

    public static final byte JMP_REL8 = (byte) 0xEB;
    public static final byte JMP_REL32 = (byte) 0xE9;
    public static final byte JMP_R = (byte) 0xFF;

    public static final byte LAHF = (byte) 0x9F;

    public static final byte LEA = (byte) 0x8D;

    public static final byte LEAVE = (byte) 0xC9;

    public static final byte LODSB = (byte) 0xAC;
    public static final byte LODSD = (byte) 0xAD;

    public static final byte MAXSD_X_XM = 0x5F;

    public static final byte MINSD_X_XM = 0x5D;

    public static final byte MOV_RM_R8 = (byte) 0x88;
    public static final byte MOV_RM_R = (byte) 0x89;
    public static final byte MOV_RM_I = (byte) 0xC7;
    public static final byte MOV_R8_RM8 = (byte) 0x8A;
    public static final byte MOV_R_RM = (byte) 0x8B;
    public static final byte MOV_RM_I8 = (byte) 0xC6;
    public static final byte MOV_R8_I = (byte) 0xB0;
    public static final byte MOV_R_I = (byte) 0xB8;

    public static final byte MOVAPS_X_XM = 0x28;
    public static final byte MOVAPS_XM_X = 0x29;

    public static final byte MOVD_X_RM = 0x6E;

    public static final byte MOVDQA_X_XM = 0x6F;
    public static final byte MOVDQA_XM_X = 0x7F;

    public static final byte MOVDQU_X_XM = 0x6F;
    public static final byte MOVDQU_XM_X = 0x7F;

    public static final byte MOVHPD_X_M64 = 0x16;
    public static final byte MOVHPD_M64_X = 0x17;

    public static final byte MOVLPD_X_M64 = 0x12;
    public static final byte MOVLPD_M64_X = 0x13;

    public static final byte MOVMSKPD_R_XM = 0x50;

    public static final byte MOVNTDQ_M128_X = (byte) 0xE7;

    public static final byte MOVQ_X_XM = 0x7E;
    public static final byte MOVQ_XM_X = (byte) 0xD6;

    public static final byte MOVSB = (byte) 0xA4;
    public static final byte MOVSD = (byte) 0xA5;

    public static final byte MOVSX_R_RM8 = (byte) 0xBE;
    public static final byte MOVSX_R_RM16 = (byte) 0xBF;
    public static final byte MOVSXD_R_RM = 0x63;

    public static final byte MOVUPS_X_XM = 0x10;
    public static final byte MOVUPS_XM_X = 0x11;

    public static final byte MOVZX_R_RM8 = (byte) 0xB6;
    public static final byte MOVZX_R_RM16 = (byte) 0xB7;

    public static final byte MUL_RM8 = (byte) 0xF6;
    public static final byte MUL_RM = (byte) 0xF7;

    public static final byte MULSD_X_XM = 0x59;

    public static final byte NOP = (byte) 0x90;
    public static final byte NOP_RM = 0x1F;

    public static final byte OR_A_I8 = 0x0C;
    public static final byte OR_A_I = 0x0D;
    public static final byte OR_RM8_I = (byte) 0x80;
    public static final byte OR_RM8_R8 = 0x08;
    public static final byte OR_RM_R = 0x09;
    public static final byte OR_R8_RM8 = 0x0A;
    public static final byte OR_R_RM = 0x0B;

    public static final byte ORPD_X_XM = 0x56;

    public static final byte PADDB_X_XM = (byte) 0xFC;
    public static final byte PADDW_X_XM = (byte) 0xFD;
    public static final byte PADDD_X_XM = (byte) 0xFE;
    public static final byte PADDQ_X_XM = (byte) 0xD4;

    public static final byte PAND_X_XM = (byte) 0xDB;

    public static final byte PANDN_X_XM = (byte) 0xDF;

    public static final byte PCMPEQB_X_XM = 0x74;
    public static final byte PCMPEQW_X_XM = 0x75;
    public static final byte PCMPEQD_X_XM = 0x76;

    public static final byte PCMPGTB_X_XM = 0x64;
    public static final byte PCMPGTW_X_XM = 0x65;
    public static final byte PCMPGTD_X_XM = 0x66;

    public static final byte PMAXUB_X_XM = (byte) 0xDE;

    public static final byte PMINUB_X_XM = (byte) 0xDA;

    public static final byte PMOVMSKB_R_X = (byte) 0xD7;

    public static final byte PMULUDQ_X_XM = (byte) 0xF4;

    public static final byte POP_R = 0x58;

    public static final byte POP_RM = (byte) 0x8F;

    public static final byte POPF = (byte) 0x9D;

    public static final byte POR_X_XM = (byte) 0xEB;

    public static final byte PREFETCH = 0x18;

    public static final byte PSHUFD = 0x70;

    public static final byte PSLLD_XM_I = 0x72;

    public static final byte PSLLDQ = 0x73;

    public static final byte PSUBB = (byte) 0xF8;
    public static final byte PSUBD = (byte) 0xFA;

    public static final byte PSUBUSB = (byte) 0xD8;
    public static final byte PSUBUSW = (byte) 0xD9;

    public static final byte PUNPCKHBW = 0x68;
    public static final byte PUNPCKHWD = 0x69;
    public static final byte PUNPCKHDQ = 0x6A;

    public static final byte PUNPCKLBW = 0x60;
    public static final byte PUNPCKLWD = 0x61;
    public static final byte PUNPCKLDQ = 0x62;

    public static final byte PUSH_R = 0x50;
    public static final byte PUSH_FS = (byte) 0xA0;
    public static final byte PUSH_GS = (byte) 0xA8;
    public static final byte PUSH_I8 = 0x6A;
    public static final byte PUSH_I = 0x68;

    public static final byte PUSHF = (byte) 0x9C;

    public static final byte PXOR_X_XM = (byte) 0xEF;

    public static final byte RDTSC = 0x31;

    public static final byte RET_FAR = (byte) 0xCB;
    public static final byte RET_NEAR = (byte) 0xC3;

    public static final byte SAHF = (byte) 0x9E;

    public static final byte SBB_RM_R = 0x19;

    public static final byte SCASB = (byte) 0xAE;
    public static final byte SCAS = (byte) 0xAF;

    public static final byte SETA = (byte) 0x97;
    public static final byte SETAE = (byte) 0x93;
    public static final byte SETB = (byte) 0x92;
    public static final byte SETBE = (byte) 0x96;
    public static final byte SETE = (byte) 0x94;
    public static final byte SETG = (byte) 0x9F;
    public static final byte SETGE = (byte) 0x9D;
    public static final byte SETL = (byte) 0x9C;
    public static final byte SETLE = (byte) 0x9E;
    public static final byte SETNE = (byte) 0x95;
    public static final byte SETNO = (byte) 0x91;
    public static final byte SETNP = (byte) 0x9B;
    public static final byte SETNS = (byte) 0x99;
    public static final byte SETO = (byte) 0x90;
    public static final byte SETP = (byte) 0x9A;
    public static final byte SETS = (byte) 0x98;

    public static final byte SFENCE = (byte) 0xF8;

    public static final byte SHL_RM8_1 = (byte) 0xD0;
    public static final byte SHL_RM_1 = (byte) 0xD1;
    public static final byte SHL_RM8_I8 = (byte) 0xC0;
    public static final byte SHL_RM_I = (byte) 0xC1;
    public static final byte SHL_RM_C = (byte) 0xD3;

    public static final byte SHLD_RM_R_C = (byte) 0xA5;

    public static final byte SHRD_RM_R_C = (byte) 0xAD;

    public static final byte SHUFPS_X_XM_I8 = (byte) 0xC6;

    public static final byte SQRTSD_X_XM = 0x51;

    public static final byte STOSB = (byte) 0xAA;
    public static final byte STOS = (byte) 0xAB;

    public static final byte SUB_A_I8 = 0x2C;
    public static final byte SUB_A_I = 0x2D;
    public static final byte SUB_RM8_R8 = 0x28;
    public static final byte SUB_RM_R = 0x29;
    public static final byte SUB_RM_I = (byte) 0x81;
    public static final byte SUB_RM_I8 = (byte) 0x83;
    public static final byte SUB_R_RM8 = 0x2A;
    public static final byte SUB_R_RM = 0x2B;

    public static final byte SUBSD_X_XM = 0x5C;

    public static final byte TEST_AL_I = (byte) 0xA8;
    public static final byte TEST_A_I = (byte) 0xA9;
    public static final byte TEST_RM_R8 = (byte) 0x84;
    public static final byte TEST_RM_R = (byte) 0x85;

    public static final byte UCOMISD_X_XM = 0x2E;

    public static final byte UNPCKHPD_X_XM = 0x15;

    public static final byte UNPCKLPD_X_XM = 0x14;

    public static final byte XADD_RM8_R8 = (byte) 0xC0;
    public static final byte XADD_RM_R = (byte) 0xC1;

    public static final byte XCHG_A_R = (byte) 0x90;
    public static final byte XCHG_RM8_R8 = (byte) 0x86;
    public static final byte XCHG_RM_R = (byte) 0x87;

    public static final byte XOR_A_I8 = 0x34;
    public static final byte XOR_A_I = 0x35;
    public static final byte XOR_RM8_R8 = 0x30;
    public static final byte XOR_RM_R = 0x31;
    public static final byte XOR_R8_RM8 = 0x32;
    public static final byte XOR_R_RM = 0x33;

    public static final byte XORPD_X_XM = 0x57;

    // PREFIX: 0x0F (ESCAPE)
    public static final byte SYSCALL = 0x05;
}
