package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class MunmapRecord extends Record {
    public static final int MAGIC = 0x554d4150;

    private long addr;
    private long len;
    private int result;

    MunmapRecord() {
        super(MAGIC);
    }

    public MunmapRecord(long addr, long len, int result) {
        this();
        this.addr = addr;
        this.len = len;
        this.result = result;
    }

    public long getAddress() {
        return addr;
    }

    public long getLength() {
        return len;
    }

    @Override
    protected int size() {
        return 2 * 8 + 4;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        addr = in.read64bit();
        len = in.read64bit();
        result = in.read32bit();
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(addr);
        out.write64bit(len);
        out.write32bit(result);
    }

    @Override
    public String toString() {
        return String.format("munmap(0x%016x, %d) = 0x%08x", addr, len, result);
    }
}
