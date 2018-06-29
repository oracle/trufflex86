package org.graalvm.vm.x86.emu;

public class Registers {
    public long rax;
    public long rbx;
    public long rcx;
    public long rdx;
    public long rsi;
    public long rdi;
    public long rbp;
    public long rsp;
    public long r8;
    public long r9;
    public long r10;
    public long r11;
    public long r12;
    public long r13;
    public long r14;
    public long r15;
    public long rip;
    public long rflags;
    public long fs_base;
    public long gs_base;
    public long mxcsr;
    public final byte[] xmms = new byte[256];
}
