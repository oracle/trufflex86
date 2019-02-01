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

import org.graalvm.vm.posix.api.mem.Mman;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

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
