package org.graalvm.vm.x86.emu;

import java.io.Closeable;

public class Ptrace implements Closeable {
    private static native int fork(String cmd);

    private static native int waitForSignal(int pid);

    private static native long syscall(int pid, long nr, long a1, long a2, long a3, long a4, long a5, long a6);

    private static native long read(int pid, long addr);

    private static native void write(int pid, long addr, long value);

    private static native void kill(int pid, int signal);

    private static native void readRegisters(int pid, Registers regs);

    private static native void writeRegisters(int pid, Registers regs);

    private final int pid;

    public Ptrace() {
        this("/bin/sh"); // always available
    }

    public Ptrace(String name) {
        pid = fork(name);
    }

    public long read(long addr) {
        return read(pid, addr);
    }

    public void write(long addr, long value) {
        write(pid, addr, value);
    }

    public long syscall(int nr, long a1, long a2, long a3, long a4, long a5, long a6) {
        return syscall(pid, nr, a1, a2, a3, a4, a5, a6);
    }

    public Registers getRegisters() {
        Registers regs = new Registers();
        readRegisters(pid, regs);
        return regs;
    }

    public void setRegisters(Registers regs) {
        writeRegisters(pid, regs);
    }

    public int getPid() {
        return pid;
    }

    @Override
    public void close() {
        kill(pid, 9); // 9 = SIGKILL
    }
}
