package org.graalvm.vm.x86.isa;

import org.graalvm.vm.memory.VirtualMemory;

public class CodeMemoryReader extends CodeReader {
    private VirtualMemory memory;
    private long pc;

    public CodeMemoryReader(VirtualMemory memory, long pc) {
        this.memory = memory;
        this.pc = pc;
    }

    @Override
    public byte read8() {
        return memory.getI8(pc++);
    }

    @Override
    public short read16() {
        short value = memory.getI16(pc);
        pc += 2;
        return value;
    }

    @Override
    public int read32() {
        int value = memory.getI32(pc);
        pc += 4;
        return value;
    }

    @Override
    public long read64() {
        long value = memory.getI64(pc);
        pc += 4;
        return value;
    }

    @Override
    public long getPC() {
        return pc;
    }

    @Override
    public void setPC(long pc) {
        this.pc = pc;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
