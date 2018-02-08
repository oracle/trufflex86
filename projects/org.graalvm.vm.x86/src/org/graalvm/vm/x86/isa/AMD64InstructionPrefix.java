package org.graalvm.vm.x86.isa;

public class AMD64InstructionPrefix {
    public static final byte LOCK = (byte) 0xF0;
    public static final byte REPNE = (byte) 0xF2;
    public static final byte REPNZ = (byte) 0xF2;
    public static final byte REP = (byte) 0xF3;
    public static final byte REPE = (byte) 0xF3;
    public static final byte REPZ = (byte) 0xF3;
    public static final byte BND = (byte) 0xF2;

    public static final byte SEGMENT_OVERRIDE_CS = 0x2e;
    public static final byte SEGMENT_OVERRIDE_SS = 0x36;
    public static final byte SEGMENT_OVERRIDE_DS = 0x3e;
    public static final byte SEGMENT_OVERRIDE_ES = 0x26;
    public static final byte SEGMENT_OVERRIDE_FS = 0x64;
    public static final byte SEGMENT_OVERRIDE_GS = 0x65;
    public static final byte BRANCH_HINT_NOT_TAKEN = 0x2e;
    public static final byte BRANCH_HINT_TAKEN = 0x3e;

    public static final byte OPERAND_SIZE_OVERRIDE = 0x66;

    public static final byte ADDRESS_SIZE_OVERRIDE = 0x67;
}
