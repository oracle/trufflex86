package org.graalvm.vm.x86;

public class Cpuid {
    // @formatter:off
    /* Intel-defined CPU features, CPUID level 0x00000001 (EDX), word 0 */
    public static final int X86_FEATURE_FPU       = ( 0*32+ 0); /* Onboard FPU */
    public static final int X86_FEATURE_VME       = ( 0*32+ 1); /* Virtual Mode Extensions */
    public static final int X86_FEATURE_DE        = ( 0*32+ 2); /* Debugging Extensions */
    public static final int X86_FEATURE_PSE       = ( 0*32+ 3); /* Page Size Extensions */
    public static final int X86_FEATURE_TSC       = ( 0*32+ 4); /* Time Stamp Counter */
    public static final int X86_FEATURE_MSR       = ( 0*32+ 5); /* Model-Specific Registers */
    public static final int X86_FEATURE_PAE       = ( 0*32+ 6); /* Physical Address Extensions */
    public static final int X86_FEATURE_MCE       = ( 0*32+ 7); /* Machine Check Exception */
    public static final int X86_FEATURE_CX8       = ( 0*32+ 8); /* CMPXCHG8 instruction */
    public static final int X86_FEATURE_APIC      = ( 0*32+ 9); /* Onboard APIC */
    public static final int X86_FEATURE_SEP       = ( 0*32+11); /* SYSENTER/SYSEXIT */
    public static final int X86_FEATURE_MTRR      = ( 0*32+12); /* Memory Type Range Registers */
    public static final int X86_FEATURE_PGE       = ( 0*32+13); /* Page Global Enable */
    public static final int X86_FEATURE_MCA       = ( 0*32+14); /* Machine Check Architecture */
    public static final int X86_FEATURE_CMOV      = ( 0*32+15); /* CMOV instructions (plus FCMOVcc, FCOMI with FPU) */
    public static final int X86_FEATURE_PAT       = ( 0*32+16); /* Page Attribute Table */
    public static final int X86_FEATURE_PSE36     = ( 0*32+17); /* 36-bit PSEs */
    public static final int X86_FEATURE_PN        = ( 0*32+18); /* Processor serial number */
    public static final int X86_FEATURE_CLFLUSH   = ( 0*32+19); /* CLFLUSH instruction */
    public static final int X86_FEATURE_DS        = ( 0*32+21); /* "dts" Debug Store */
    public static final int X86_FEATURE_ACPI      = ( 0*32+22); /* ACPI via MSR */
    public static final int X86_FEATURE_MMX       = ( 0*32+23); /* Multimedia Extensions */
    public static final int X86_FEATURE_FXSR      = ( 0*32+24); /* FXSAVE/FXRSTOR, CR4.OSFXSR */
    public static final int X86_FEATURE_XMM       = ( 0*32+25); /* "sse" */
    public static final int X86_FEATURE_XMM2      = ( 0*32+26); /* "sse2" */
    public static final int X86_FEATURE_SELFSNOOP = ( 0*32+27); /* "ss" CPU self snoop */
    public static final int X86_FEATURE_HT        = ( 0*32+28); /* Hyper-Threading */
    public static final int X86_FEATURE_ACC       = ( 0*32+29); /* "tm" Automatic clock control */
    public static final int X86_FEATURE_IA64      = ( 0*32+30); /* IA-64 processor */
    public static final int X86_FEATURE_PBE       = ( 0*32+31); /* Pending Break Enable */
    // @formatter:on
}
