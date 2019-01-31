package org.graalvm.vm.x86.isa;

import org.graalvm.vm.posix.elf.ProgramHeader;

public class CodeSegmentReader extends CodeReader {
    private long vaddr;
    private long faddr;
    private long vstart;
    private long fstart;
    private long end;
    private byte[] elf;

    public CodeSegmentReader(ProgramHeader hdr) {
        vaddr = hdr.getVirtualAddress();
        faddr = hdr.getOffset();
        vstart = vaddr;
        fstart = faddr;
        end = faddr + hdr.getFileSize();
        elf = hdr.getElf().getData();
    }

    @Override
    public long getPC() {
        return vaddr;
    }

    @Override
    public void setPC(long pc) {
        vaddr = pc;
        long delta = pc - vstart;
        faddr = fstart + delta;
    }

    @Override
    public byte read8() {
        assert (int) faddr == faddr;
        byte value = elf[(int) faddr];
        faddr++;
        vaddr++;
        return value;
    }

    @Override
    public boolean isAvailable() {
        return faddr < end;
    }
}
