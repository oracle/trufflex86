package org.graalvm.vm.x86.isa;

import com.everyware.posix.elf.ProgramHeader;

public class CodeSegmentReader extends CodeReader {
    private long vaddr;
    private long faddr;
    private long end;
    private byte[] elf;

    public CodeSegmentReader(ProgramHeader hdr) {
        vaddr = hdr.getVirtualAddress();
        faddr = hdr.getOffset();
        end = faddr + hdr.getFileSize();
        elf = hdr.getElf().getData();
    }

    public long getVirtualAddress() {
        return vaddr;
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
