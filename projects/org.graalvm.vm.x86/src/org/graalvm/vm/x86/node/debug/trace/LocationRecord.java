package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.memory.util.HexFormatter;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class LocationRecord extends Record {
    public static final int MAGIC = 0x4c4f4330; // LOC0

    private String filename;
    private String symbol;
    private byte[] machinecode;
    private String[] assembly;
    private long offset;
    private long pc;

    LocationRecord() {
        super(MAGIC);
    }

    public LocationRecord(String filename, String symbol, long offset, long pc, byte[] machinecode, String[] assembly) {
        this();
        this.filename = filename;
        this.symbol = symbol;
        this.machinecode = machinecode;
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

    public byte[] getMachinecode() {
        return machinecode;
    }

    public void setMachinecode(byte[] machinecode) {
        this.machinecode = machinecode;
    }

    public String[] getAssembly() {
        return assembly;
    }

    public void setAssembly(String[] assembly) {
        this.assembly = assembly;
    }

    public String getMnemonic() {
        if (assembly == null || assembly.length < 1) {
            return null;
        } else {
            return assembly[0];
        }
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
        int size = 2 * 8;
        size += sizeString(filename);
        size += sizeString(symbol);
        size += sizeStringArray(assembly);
        size += sizeArray(machinecode);
        return size;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        filename = readString(in);
        symbol = readString(in);
        assembly = readStringArray(in);
        machinecode = readArray(in);

        offset = in.read64bit();
        pc = in.read64bit();
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        writeString(out, filename);
        writeString(out, symbol);
        writeStringArray(out, assembly);
        writeArray(out, machinecode);
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

    public String getDisassembly() {
        if (assembly == null) {
            return null;
        }
        if (assembly.length == 1) {
            return assembly[0];
        } else {
            return assembly[0] + "\t" + Stream.of(assembly).skip(1).collect(Collectors.joining(","));
        }
    }

    public String getPrintableBytes() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < machinecode.length; i++) {
            buf.append(' ');
            buf.append(HexFormatter.tohex(Byte.toUnsignedInt(machinecode[i]), 2));
        }
        return buf.toString().substring(1);
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
        if (assembly != null) {
            buf.append(getDisassembly());
            buf.append(" ; ");
            buf.append(getPrintableBytes());
        }
        return buf.toString();
    }
}
