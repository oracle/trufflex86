package org.graalvm.vm.x86.node.debug.trace;

import java.io.IOException;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.memory.vector.Vector128;

import com.everyware.util.io.WordInputStream;
import com.everyware.util.io.WordOutputStream;

public class MemoryEventRecord extends Record {
    public static final int MAGIC = 0x4d454d30; // MEM0

    private boolean read;
    private long address;
    private byte size;
    private long value64;
    private Vector128 value128;

    public MemoryEventRecord() {
        super(MAGIC);
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
        if (size == 16) {
            return 8 + 4 + 1 + 16;
        } else {
            return 2 * 8 + 4 + 1;
        }
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        read = in.read8bit() != 0;
        address = in.read64bit();
        size = (byte) in.read8bit();
        if (size == 16) {
            long hi = in.read64bit();
            long lo = in.read64bit();
            value128 = new Vector128(hi, lo);
        } else {
            value64 = in.read64bit();
        }
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        out.write8bit((byte) (read ? 1 : 0));
        out.write64bit(address);
        out.write32bit(size);
        if (size == 16) {
            out.write64bit(value128.getI64(0));
            out.write64bit(value128.getI64(1));
        } else {
            out.write64bit(value64);
        }
    }

    @Override
    public String toString() {
        String str = null;
        StringBuilder val = new StringBuilder("0x");
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
        if (str != null) {
            val.append(", '").append(str).append("'");
        }
        return "Memory access to 0x" + HexFormatter.tohex(address, 16) + ": " + (read ? "read" : "write") + " " + size + " bytes (" + val + ")";
    }
}
