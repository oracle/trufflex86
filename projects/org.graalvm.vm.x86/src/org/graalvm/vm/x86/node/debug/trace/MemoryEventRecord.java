package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.memory.vector.Vector128;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class MemoryEventRecord extends Record {
    public static final int MAGIC = 0x4d454d30; // MEM0

    private static final byte FLAG_DATA = 1;
    private static final byte FLAG_WRITE = 2;

    private boolean write;
    private boolean data;
    private long address;
    private byte size;
    private long value64;
    private Vector128 value128;

    MemoryEventRecord() {
        super(MAGIC);
    }

    public MemoryEventRecord(long address, boolean write, int size) {
        this();
        this.address = address;
        this.write = write;
        this.size = (byte) size;
        this.data = false;
    }

    public MemoryEventRecord(long address, boolean write, int size, long value) {
        this();
        this.address = address;
        this.write = write;
        this.size = (byte) size;
        this.data = true;
        this.value64 = value;
    }

    public MemoryEventRecord(long address, boolean write, int size, Vector128 value) {
        this();
        this.address = address;
        this.write = write;
        this.size = (byte) size;
        this.data = true;
        this.value128 = value;
    }

    public boolean hasData() {
        return data;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public int getSize() {
        return size;
    }

    public long getValue() {
        return value64;
    }

    public Vector128 getVector() {
        return value128;
    }

    public void setValue(byte value) {
        this.size = 1;
        this.value64 = value;
    }

    public void setValue(short value) {
        this.size = 2;
        this.value64 = value;
    }

    public void setValue(int value) {
        this.size = 4;
        this.value64 = value;
    }

    public void setValue(long value) {
        this.size = 8;
        this.value64 = value;
    }

    public void setValue(Vector128 value) {
        this.size = 16;
        this.value128 = value;
    }

    @Override
    protected int size() {
        return 8 + 2 + (data ? size : 0);
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        int flags = in.read8bit();
        write = (flags & FLAG_WRITE) != 0;
        data = (flags & FLAG_DATA) != 0;
        size = (byte) in.read8bit();
        address = in.read64bit();
        if (data) {
            switch (size) {
                case 1:
                    value64 = in.read8bit();
                    break;
                case 2:
                    value64 = in.read16bit();
                    break;
                case 4:
                    value64 = in.read32bit();
                    break;
                case 8:
                    value64 = in.read64bit();
                    break;
                case 16: {
                    long hi = in.read64bit();
                    long lo = in.read64bit();
                    value128 = new Vector128(hi, lo);
                    break;
                }
                default:
                    throw new IOException("unknown size: " + size);
            }
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        int flags = 0;
        if (write) {
            flags |= FLAG_WRITE;
        }
        if (data) {
            flags |= FLAG_DATA;
        }
        out.write8bit((byte) flags);
        out.write8bit(size);
        out.write64bit(address);
        if (data) {
            switch (size) {
                case 1:
                    out.write8bit((byte) value64);
                    break;
                case 2:
                    out.write16bit((short) value64);
                    break;
                case 4:
                    out.write32bit((int) value64);
                    break;
                case 8:
                    out.write64bit(value64);
                    break;
                case 16:
                    out.write64bit(value128.getI64(0));
                    out.write64bit(value128.getI64(1));
                    break;
                default:
                    throw new IOException("unknown size: " + size);
            }
        }
    }

    @Override
    public String toString() {
        String str = null;
        StringBuilder val = new StringBuilder("0x");
        if (data) {
            switch (size) {
                case 1:
                    str = Stringify.i8((byte) value64);
                    val.append(HexFormatter.tohex(value64, 2));
                    break;
                case 2:
                    str = Stringify.i16((short) value64);
                    val.append(HexFormatter.tohex(value64, 4));
                    break;
                case 4:
                    str = Stringify.i32((int) value64);
                    val.append(HexFormatter.tohex(value64, 8));
                    break;
                case 8:
                    str = Stringify.i64(value64);
                    val.append(HexFormatter.tohex(value64, 16));
                    break;
                case 16:
                    str = Stringify.i128(value128);
                    val.append(HexFormatter.tohex(value128.getI64(0), 16));
                    val.append(HexFormatter.tohex(value128.getI64(1), 16));
                    break;
            }
        }
        if (str != null) {
            val.append(", '").append(str).append("'");
        }
        return "Memory access to 0x" + HexFormatter.tohex(address, 16) + ": " + (!write ? "read" : "write") + " " + size + " bytes" + (data ? " (" + val + ")" : "");
    }
}
