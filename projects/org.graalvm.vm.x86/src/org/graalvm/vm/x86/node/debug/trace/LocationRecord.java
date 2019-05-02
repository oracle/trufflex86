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

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class LocationRecord extends Record {
    public static final int MAGIC = 0x4c4f4330; // LOC0

    private String filename;
    private String symbol;
    private byte[] machinecode;
    private String[] assembly;
    private long offset;
    private long pc;

    private byte[] filenameCache;
    private byte[] symbolCache;
    private byte[][] assemblyCache;

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
    protected int getDataSize() {
        int size = 2 * 8;
        filenameCache = filename != null ? filename.getBytes() : null;
        symbolCache = symbol != null ? symbol.getBytes() : null;
        if (assembly != null) {
            assemblyCache = new byte[assembly.length][];
            for (int i = 0; i < assembly.length; i++) {
                assemblyCache[i] = assembly[i].getBytes();
            }
        }

        // size += sizeString(filename);
        // size += sizeString(symbol);
        // size += sizeStringArray(assembly);
        size += sizeShortArray(filenameCache);
        size += sizeShortArray(symbolCache);
        size += sizeShortArray2(assemblyCache);
        size += sizeShortArray(machinecode);
        return size;
    }

    @Override
    protected void readRecord(WordInputStream in) throws IOException {
        filename = readString(in);
        symbol = readString(in);
        assembly = readStringArray(in);
        machinecode = readShortArray(in);

        offset = in.read64bit();
        pc = in.read64bit();
    }

    @Override
    protected void writeRecord(WordOutputStream out) throws IOException {
        // writeString(out, filename);
        // writeString(out, symbol);
        // writeStringArray(out, assembly);
        writeShortArray(out, filenameCache);
        writeShortArray(out, symbolCache);
        writeShortArray2(out, assemblyCache);
        writeShortArray(out, machinecode);
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
