/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.node.debug.trace;

import java.io.EOFException;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.isa.CpuState;

public abstract class Record {
    private static final Logger log = Trace.create(Record.class);

    private final int magic;
    private Supplier<CpuState> lastState;

    protected Record(int magic) {
        this.magic = magic;
    }

    protected CpuState getLastState() {
        return lastState.get();
    }

    protected Supplier<CpuState> getLastStateSupplier() {
        return lastState;
    }

    protected void setLastState(CpuState state) {
        lastState = () -> state;
    }

    protected void setLastState(Supplier<CpuState> state) {
        lastState = state;
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Record> T read(WordInputStream in, Supplier<CpuState> lastState) throws IOException {
        int type;
        try {
            type = in.read32bit();
        } catch (EOFException e) {
            return null;
        }
        int size = in.read32bit();
        long start = in.tell();

        Record record = null;
        switch (type) {
            case FullCpuStateRecord.MAGIC:
                record = new FullCpuStateRecord();
                break;
            case DeltaCpuStateRecord.MAGIC:
                record = new DeltaCpuStateRecord();
                break;
            case LocationRecord.MAGIC:
                record = new LocationRecord();
                break;
            case MemoryEventRecord.MAGIC:
                record = new MemoryEventRecord();
                break;
            case StepRecord.MAGIC:
                record = new StepRecord();
                break;
            case CallArgsRecord.MAGIC:
                record = new CallArgsRecord();
                break;
            case SystemLogRecord.MAGIC:
                record = new SystemLogRecord();
                break;
            case MmapRecord.MAGIC:
                record = new MmapRecord();
                break;
            case MunmapRecord.MAGIC:
                record = new MunmapRecord();
                break;
            case MprotectRecord.MAGIC:
                record = new MprotectRecord();
                break;
            case BrkRecord.MAGIC:
                record = new BrkRecord();
                break;
            case EofRecord.MAGIC:
                record = new EofRecord();
                break;
        }
        if (record != null) {
            record.lastState = lastState;
            record.readRecord(in);
        } else {
            log.warning("Unknown record: 0x" + HexFormatter.tohex(type, 8));
            in.skip(size);
        }

        long end = in.tell();
        long sz = end - start;
        if (sz != size) {
            throw new IOException("Error: invalid size (" + size + " vs " + sz + "; " + (record != null ? record.getClass().getSimpleName() : "unknown class") + ")");
        }

        return (T) record;
    }

    public final void write(WordOutputStream out) throws IOException {
        int size = size();
        out.write32bit(magic);
        out.write32bit(size);
        long start = out.tell();
        writeRecord(out);
        long end = out.tell();
        long sz = end - start;
        if (sz != size) {
            throw new IOException("Error: invalid size (" + size + " vs " + sz + ")");
        }
    }

    protected abstract int size();

    protected abstract void readRecord(WordInputStream in) throws IOException;

    protected abstract void writeRecord(WordOutputStream out) throws IOException;

    protected static final int sizeString(String s) {
        if (s == null) {
            return 2;
        } else {
            return 2 + s.getBytes().length;
        }
    }

    protected static final String readString(WordInputStream in) throws IOException {
        int length = in.read16bit();
        if (length == 0) {
            return null;
        } else {
            byte[] bytes = new byte[length];
            in.read(bytes);
            return new String(bytes);
        }
    }

    protected static final void writeString(WordOutputStream out, String s) throws IOException {
        if (s == null) {
            out.write16bit((short) 0);
        } else {
            byte[] bytes = s.getBytes();
            out.write16bit((short) bytes.length);
            out.write(bytes);
        }
    }

    protected static final int sizeStringArray(String[] s) {
        if (s == null) {
            return 2;
        } else {
            int size = 2;
            for (String part : s) {
                size += sizeString(part);
            }
            return size;
        }
    }

    protected static final String[] readStringArray(WordInputStream in) throws IOException {
        int length = in.read16bit();
        if (length == 0) {
            return null;
        } else {
            String[] result = new String[length];
            for (int i = 0; i < result.length; i++) {
                int slen = in.read16bit();
                if (slen == 0) {
                    result[i] = null;
                } else {
                    byte[] bytes = new byte[slen];
                    in.read(bytes);
                    result[i] = new String(bytes);
                }
            }
            return result;
        }
    }

    protected static final void writeStringArray(WordOutputStream out, String[] data) throws IOException {
        if (data == null) {
            out.write16bit((short) 0);
        } else {
            out.write16bit((short) data.length);
            for (int i = 0; i < data.length; i++) {
                writeString(out, data[i]);
            }
        }
    }

    protected static final int sizeArray(byte[] data) {
        if (data == null) {
            return 4;
        } else {
            return data.length + 4;
        }
    }

    protected static final byte[] readArray(WordInputStream in) throws IOException {
        int length = in.read32bit();
        if (length == 0) {
            return null;
        } else {
            byte[] data = new byte[length];
            in.read(data);
            return data;
        }
    }

    protected static final void writeArray(WordOutputStream out, byte[] data) throws IOException {
        if (data == null) {
            out.write32bit(0);
        } else {
            out.write32bit(data.length);
            out.write(data);
        }
    }

    protected static final int sizeShortArray(byte[] data) {
        if (data == null) {
            return 2;
        } else {
            return data.length + 2;
        }
    }

    protected static final byte[] readShortArray(WordInputStream in) throws IOException {
        int length = in.read16bit();
        if (length == 0) {
            return null;
        } else {
            byte[] data = new byte[length];
            in.read(data);
            return data;
        }
    }

    protected static final void writeShortArray(WordOutputStream out, byte[] data) throws IOException {
        if (data == null) {
            out.write16bit((short) 0);
        } else {
            out.write16bit((short) data.length);
            out.write(data);
        }
    }

    protected static final int sizeShortArray2(byte[][] data) {
        if (data == null) {
            return 2;
        } else {
            int size = 2;
            for (int i = 0; i < data.length; i++) {
                size += 2 + data[i].length;
            }
            return size;
        }
    }

    protected static final byte[][] readShortArray2(WordInputStream in) throws IOException {
        int length = in.read16bit();
        if (length == 0) {
            return null;
        } else {
            byte[][] data = new byte[length][];
            for (int i = 0; i < data.length; i++) {
                data[i] = new byte[in.read16bit()];
                in.read(data[i]);
            }
            return data;
        }
    }

    protected static final void writeShortArray2(WordOutputStream out, byte[][] data) throws IOException {
        if (data == null) {
            out.write16bit((short) 0);
        } else {
            out.write16bit((short) data.length);
            for (int i = 0; i < data.length; i++) {
                out.write16bit((short) data[i].length);
                out.write(data[i]);
            }
        }
    }
}
