package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

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
            size += 8 + 2 + memory[i].length;
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
            memory[i] = new byte[in.read16bit()];
            if (memory[i].length > 0) {
                in.read(memory[i]);
            }
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write64bit(pc);
        writeString(out, symbol);
        out.write8bit((byte) args.length);
        for (int i = 0; i < args.length; i++) {
            out.write64bit(args[i]);
            out.write16bit((short) memory[i].length);
            out.write(memory[i]);
        }
    }
}
