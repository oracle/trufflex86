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
package org.graalvm.vm.posix.api;

public class Dirent implements Struct {
    public static final byte DT_UNKNOWN = 0;
    public static final byte DT_FIFO = 1;
    public static final byte DT_CHR = 2;
    public static final byte DT_DIR = 4;
    public static final byte DT_BLK = 6;
    public static final byte DT_REG = 8;
    public static final byte DT_LNK = 10;
    public static final byte DT_SOCK = 12;
    public static final byte DT_WHT = 14;

    public static final int DIRENT_32 = 0;
    public static final int DIRENT_64 = 1;
    public static final int DIRENT64 = 2;

    public long d_ino;
    public long d_off;
    public short d_reclen;
    public byte d_type;
    public String d_name;

    public static byte IFTODT(int mode) {
        return (byte) ((mode & 0170000) >>> 12);
    }

    public static int DTTOIF(int dirtype) {
        return dirtype << 12;
    }

    public int size32() {
        d_reclen = (short) (12 + d_name.length());
        return Short.toUnsignedInt(d_reclen);
    }

    public int size64() {
        d_reclen = (short) (20 + d_name.length());
        return Short.toUnsignedInt(d_reclen);
    }

    @Override
    public PosixPointer write32(PosixPointer p) {
        int len = size32();
        PosixPointer ptr = p;
        ptr.setI32((int) d_ino);
        ptr = ptr.add(4);
        ptr.setI32((int) d_off);
        ptr = ptr.add(4);
        ptr.setI16((short) len);
        ptr = ptr.add(2);
        ptr = CString.strcpy(ptr, d_name);
        ptr.setI8(d_type);
        return ptr.add(1);
    }

    @Override
    public PosixPointer write64(PosixPointer p) {
        int len = size64();
        PosixPointer ptr = p;
        ptr.setI64(d_ino);
        ptr = ptr.add(8);
        ptr.setI64(d_off);
        ptr = ptr.add(8);
        ptr.setI16((short) len);
        ptr = ptr.add(2);
        ptr = CString.strcpy(ptr, d_name);
        ptr.setI8(d_type);
        return ptr.add(1);
    }

    public PosixPointer writeDirent64(PosixPointer p) {
        int len = size64();
        PosixPointer ptr = p;
        ptr.setI64(d_ino);
        ptr = ptr.add(8);
        ptr.setI64(d_off);
        ptr = ptr.add(8);
        ptr.setI16((short) len);
        ptr = ptr.add(2);
        ptr.setI8(d_type);
        ptr = ptr.add(1);
        ptr = CString.strcpy(ptr, d_name);
        return ptr;
    }
}
