package org.graalvm.vm.x86.isa;

public class CpuidBits {
    // FN=1: EDX
    public static final int TSC = 1 << 4;
    public static final int CMOV = 1 << 15;
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
}
