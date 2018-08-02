package org.graalvm.vm.x86.emu;

import org.graalvm.vm.memory.JavaVirtualMemory;

public class VirtualPtraceMemory extends JavaVirtualMemory {
    private Ptrace ptrace;

    public VirtualPtraceMemory(Ptrace ptrace) {
        this.ptrace = ptrace;
    }

    @Override
    public byte getI8(long addr) {
        return (byte) ptrace.read(addr);
    }

    @Override
    public void setI8(long addr, byte value) {
        long val = ptrace.read(addr);
        val = (val & ~0xFF) | Byte.toUnsignedLong(value);
        ptrace.write(addr, val);
    }

    @Override
    public short getI16(long addr) {
        return (short) ptrace.read(addr);
    }

    @Override
    public void setI16(long addr, short value) {
        long val = ptrace.read(addr);
        val = (val & ~0xFFFF) | Short.toUnsignedLong(value);
        ptrace.write(addr, val);
    }

    @Override
    public int getI32(long addr) {
        return (int) ptrace.read(addr);
    }

    @Override
    public void setI32(long addr, int value) {
        long val = ptrace.read(addr);
        val = (val & ~0xFFFFFFFFL) | Integer.toUnsignedLong(value);
        ptrace.write(addr, val);
    }

    @Override
    public long getI64(long addr) {
        return ptrace.read(addr);
    }

    @Override
    public void setI64(long addr, long value) {
        ptrace.write(addr, value);
    }
}
