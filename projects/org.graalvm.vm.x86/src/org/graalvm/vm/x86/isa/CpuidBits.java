package org.graalvm.vm.x86.isa;

public class CpuidBits {
    // FN=1: EDX
    public static final int TSC = 1 << 4;

    // FN=1: ECX
    public static final int RDRND = 1 << 30;

    // FN=7/0: EBX
    public static final int RDSEED = 1 << 18;

    // FN=80000001h: EDX
    public static final int LM = 1 << 29;

    // FN=80000001h: ECX
    public static final int LAHF = 1;
}
