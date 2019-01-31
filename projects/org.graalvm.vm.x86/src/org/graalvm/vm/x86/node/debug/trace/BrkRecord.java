package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class BrkRecord extends Record {
    public static final int MAGIC = 0x42524b30;

    private long brk;
    private long result;

    BrkRecord() {
        super(MAGIC);
    }

    public BrkRecord(long brk, long result) {
        this();
        this.brk = brk;
        this.result = result;
    }

    public long getBrk() {
        return brk;
    }

    public long getResult() {
        return result;
    }

    @Override
    protected int size() {
        return 2 * 8;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        brk = in.read64bit();
        result = in.read64bit();
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(brk);
        out.write64bit(result);
    }

    @Override
    public String toString() {
        return "brk(0x" + HexFormatter.tohex(brk, 16) + ") = 0x" + HexFormatter.tohex(result, 16);
    }
}
