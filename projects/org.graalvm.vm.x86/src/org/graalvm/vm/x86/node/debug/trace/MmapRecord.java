package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import com.everyware.posix.api.mem.Mman;
import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class MmapRecord extends Record {
    public static final int MAGIC = 0x4d4d4150; // MMAP

    private long addr;
    private long len;
    private int prot;
    private int flags;
    private int fildes;
    private long off;
    private long result;

    private byte[] data;

    MmapRecord() {
        super(MAGIC);
    }

    public MmapRecord(long addr, long len, int prot, int flags, int fildes, long off, long result) {
        this();
        this.addr = addr;
        this.len = len;
        this.prot = prot;
        this.flags = flags;
        this.fildes = fildes;
        this.off = off;
        this.result = result;
    }

    public long getAddress() {
        return addr;
    }

    public long getLength() {
        return len;
    }

    public int getProtection() {
        return prot;
    }

    public int getFlags() {
        return flags;
    }

    public int getFileDescriptor() {
        return fildes;
    }

    public long getOffset() {
        return off;
    }

    public long getResult() {
        return result;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    protected int size() {
        return 4 * 8 + 3 * 4 + sizeArray(data);
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        addr = in.read64bit();
        len = in.read64bit();
        prot = in.read32bit();
        flags = in.read32bit();
        fildes = in.read32bit();
        off = in.read64bit();
        result = in.read64bit();
        data = readArray(in);
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(addr);
        out.write64bit(len);
        out.write32bit(prot);
        out.write32bit(flags);
        out.write32bit(fildes);
        out.write64bit(off);
        out.write64bit(result);
        writeArray(out, data);
    }

    @Override
    public String toString() {
        return String.format("mmap(0x%016x, %d, %s, %s, %d, %d) = 0x%016x", addr, len, Mman.prot(prot), Mman.flags(flags), fildes, off, result);
    }
}
