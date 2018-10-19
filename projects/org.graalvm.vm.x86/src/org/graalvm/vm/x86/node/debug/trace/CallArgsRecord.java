package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.util.HexFormatter;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class CallArgsRecord extends Record {
    public static final int MAGIC = 0x41524753;

    private long pc;
    private String symbol;
    private long[] args;
    private byte[][] memory;

    CallArgsRecord() {
        super(MAGIC);
    }

    public CallArgsRecord(long pc, String symbol, long[] args, byte[][] memory) {
        this();
        this.pc = pc;
        this.symbol = symbol;
        setArgs(args, memory);
    }

    public long getPC() {
        return pc;
    }

    public void setPC(long pc) {
        this.pc = pc;
    }

    public String getSymobl() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long[] getArgs() {
        return args;
    }

    public byte[][] getMemory() {
        return memory;
    }

    public void setArgs(long[] args, byte[][] memory) {
        if (memory.length != args.length) {
            throw new IllegalArgumentException("lengths do not match");
        }
        this.args = args;
        this.memory = memory;
    }

    @Override
    protected int size() {
        int size = 8 + 1;
        size += sizeString(symbol);
        for (int i = 0; i < args.length; i++) {
            size += 8 + sizeArray(memory[i]);
        }
        return size;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        pc = in.read64bit();
        symbol = readString(in);
        args = new long[in.read8bit()];
        memory = new byte[args.length][];
        for (int i = 0; i < args.length; i++) {
            args[i] = in.read64bit();
            memory[i] = readArray(in);
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(pc);
        writeString(out, symbol);
        out.write8bit((byte) args.length);
        for (int i = 0; i < args.length; i++) {
            out.write64bit(args[i]);
            writeArray(out, memory[i]);
        }
    }

    private static boolean isPrintable(byte value) {
        return value >= 0x20 && value <= 0x7e; // ascii
    }

    private static void dump(StringBuilder buf, long address, byte[] data) {
        long ptr = address;
        // buf.append("memory at 0x");
        // buf.append(HexFormatter.tohex(ptr, 16));
        // buf.append(":\n");
        long ptr2 = ptr;
        boolean nl = true;
        for (int i = 0; i < data.length; i++) {
            nl = true;
            if (i % 16 == 0) {
                buf.append(HexFormatter.tohex(ptr, 16));
                buf.append(':');
            }
            byte u8 = data[(int) (ptr - address)];
            ptr++;
            buf.append(' ');
            buf.append(HexFormatter.tohex(Byte.toUnsignedLong(u8), 2));
            if (i % 16 == 15) {
                buf.append("   ");
                for (int j = 0; j < 16; j++) {
                    u8 = data[(int) (ptr2 - address)];
                    ptr2++;
                    char ch = (char) (u8 & 0xff);
                    if (!isPrintable(u8)) {
                        ch = '.';
                    }
                    buf.append(ch);
                }
                buf.append('\n');
                nl = false;
            }
        }
        if (nl) {
            buf.append('\n');
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Calling 0x");
        buf.append(HexFormatter.tohex(pc, 8));
        if (symbol != null) {
            buf.append(" <");
            buf.append(symbol);
            buf.append(">:\n");
        } else {
            buf.append(":\n");
        }
        for (int i = 0; i < args.length; i++) {
            buf.append("arg");
            buf.append(i);
            buf.append(" = 0x");
            buf.append(HexFormatter.tohex(args[i], 16));
            buf.append('\n');
            if (memory[i] != null && memory[i].length > 0) {
                dump(buf, args[i], memory[i]);
            }
        }
        return buf.toString();
    }
}
