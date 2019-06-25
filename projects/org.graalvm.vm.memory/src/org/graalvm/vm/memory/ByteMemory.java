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
package org.graalvm.vm.memory;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.util.io.Endianess;

public class ByteMemory extends Memory {
    private byte[] data;

    private static byte[] newArray(long size) {
        if ((int) size != size) {
            throw new OutOfMemoryError();
        } else {
            return new byte[(int) size];
        }
    }

    public ByteMemory(long size) {
        this(size, true);
    }

    public ByteMemory(long size, boolean isBE) {
        this(newArray(size), isBE);
    }

    public ByteMemory(byte[] data) {
        this(data, true);
    }

    public ByteMemory(byte[] data, boolean isBE) {
        super(isBE);
        this.data = data;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

    @Override
    protected byte i8(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return data[(int) pos];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected short i16L(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get16bitLE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected short i16B(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get16bitBE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected int i32L(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get32bitLE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected int i32B(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get32bitBE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected long i64L(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get64bitLE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected long i64B(long pos) {
        assert pos == (int) pos;
        check(pos);
        try {
            return Endianess.get64bitBE(data, (int) pos);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i8(long pos, byte val) {
        assert pos == (int) pos;
        check(pos);
        try {
            data[(int) pos] = val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i16L(long pos, short val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set16bitLE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i16B(long pos, short val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set16bitBE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i32L(long pos, int val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set32bitLE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i32B(long pos, int val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set32bitBE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i64L(long pos, long val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set64bitLE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    protected void i64B(long pos, long val) {
        assert pos == (int) pos;
        check(pos);
        try {
            Endianess.set64bitBE(data, (int) pos, val);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @Override
    public void free() {
        super.free();
        data = null;
    }

    @Override
    public void memcpy(byte[] dst, long off) {
        assert off == (int) off : String.format("Invalid offset 0x%016X", off);
        System.arraycopy(data, (int) off, dst, 0, dst.length);
    }

    @Override
    public long size() {
        return data.length;
    }
}
