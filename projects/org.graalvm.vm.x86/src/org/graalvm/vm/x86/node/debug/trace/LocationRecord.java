package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.util.HexFormatter;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class LocationRecord extends Record {
    public static final int MAGIC = 0x4c4f4330; // LOC0

    private String filename;
    private String symbol;
    private String assembly;
    private long offset;
    private long pc;

    public LocationRecord() {
        super(MAGIC);
    }

    public LocationRecord(String filename, String symbol, long offset, long pc, String assembly) {
        this();
        this.filename = filename;
        this.symbol = symbol;
        this.assembly = assembly;
        this.offset = offset;
        this.pc = pc;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getPC() {
        return pc;
    }

    public void setPC(long pc) {
        this.pc = pc;
    }

    @Override
    protected int size() {
        int size = 3 * 2 + 2 * 8;
        if (filename != null) {
            size += filename.getBytes().length;
        }
        if (symbol != null) {
            size += symbol.getBytes().length;
        }
        if (assembly != null) {
            size += assembly.getBytes().length;
        }
        return size;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        filename = readString(in);
        symbol = readString(in);
        assembly = readString(in);

        offset = in.read64bit();
        pc = in.read64bit();
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        writeString(out, filename);
        writeString(out, symbol);
        writeString(out, assembly);
        out.write64bit(offset);
        out.write64bit(pc);
    }

    private static String str(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("IN: ");
        buf.append(str(symbol));
        if (filename != null) {
            buf.append(" # ");
            buf.append(filename);
            buf.append(" @ 0x");
            buf.append(HexFormatter.tohex(offset, 8));
        }
        buf.append("\n0x");
        buf.append(HexFormatter.tohex(pc, 8));
        buf.append(":\t");
        buf.append(str(assembly));
        return buf.toString();
    }
}
