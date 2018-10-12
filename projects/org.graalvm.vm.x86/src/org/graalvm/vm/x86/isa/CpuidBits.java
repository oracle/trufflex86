package org.graalvm.vm.x86.isa;

import com.oracle.truffle.api.CompilerAsserts;

public class CpuidBits {
    // FN=1: EDX
    public static final int TSC = 1 << 4;
    public static final int CMOV = 1 << 15;
    public static final int CLFSH = 1 << 19;
    public static final int FXSR = 1 << 24;
    public static final int SSE = 1 << 25;
    public static final int SSE2 = 1 << 26;

    // FN=1: ECX
    public static final int SSE3 = 1;
    public static final int SSE41 = 1 << 19;
    public static final int SSE42 = 1 << 20;
    public static final int POPCNT = 1 << 23;
    public static final int XSAVE = 1 << 26;
    public static final int OXSAVE = 1 << 27;
    public static final int RDRND = 1 << 30;

    // FN=7/0: EBX
    public static final int RDSEED = 1 << 18;

    // FN=80000001h: EDX
    public static final int LM = 1 << 29;

    // FN=80000001h: ECX
    public static final int LAHF = 1;

    public static int[] getI32(String s, int len) {
        CompilerAsserts.neverPartOfCompilation();
        int[] i32 = new int[len];
        for (int i = 0; i < len; i++) {
            byte b1 = getI8(s, i * 4);
            byte b2 = getI8(s, i * 4 + 1);
            byte b3 = getI8(s, i * 4 + 2);
            byte b4 = getI8(s, i * 4 + 3);
            i32[i] = Byte.toUnsignedInt(b1) | Byte.toUnsignedInt(b2) << 8 | Byte.toUnsignedInt(b3) << 16 | Byte.toUnsignedInt(b4) << 24;
        }
        return i32;
    }

    public static byte getI8(String s, int offset) {
        CompilerAsserts.neverPartOfCompilation();
        if (offset >= s.length()) {
            return 0;
        } else {
            return (byte) s.charAt(offset);
        }
    }

    public static int getProcessorInfo(int type, int family, int model, int stepping) {
        CompilerAsserts.neverPartOfCompilation();
        int cpuidFamily = family & 0x0F;
        int cpuidModel = model & 0x0F;
        int cpuidStepping = stepping & 0x0F;
        int cpuidProcessorType = type & 0x0F;
        int cpuidExtendedModel = 0;
        int cpuidExtendedFamily = 0;

        if (model > 15) {
            cpuidExtendedModel = (model >> 4) & 0x0F;
            if (family != 6 && family < 16) {
                throw new IllegalArgumentException("model number too big for given family");
            }
        }
        if (family > 15) {
            cpuidFamily = 15;
            cpuidExtendedFamily = family - 15;
        }
        return cpuidStepping | (cpuidModel << 4) | (cpuidFamily << 8) | (cpuidProcessorType << 12) | (cpuidExtendedModel << 16) | (cpuidExtendedFamily << 20);
    }
}
