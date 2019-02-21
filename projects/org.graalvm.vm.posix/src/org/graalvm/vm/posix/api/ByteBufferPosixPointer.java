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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferPosixPointer implements PosixPointer {
    private final ByteBuffer buffer;
    private final long offset;
    private final long size;
    private final long realSize;
    private String name;

    public ByteBufferPosixPointer(ByteBuffer buffer, long offset, long size) {
        this(buffer, offset, size, size, "[posix-pointer]");
    }

    public ByteBufferPosixPointer(ByteBuffer buffer, long offset, long size, long realSize, String name) {
        assert size > 0;
        assert realSize > 0;
        this.buffer = buffer;
        this.offset = offset;
        this.size = size;
        this.realSize = realSize;
        buffer.order(ByteOrder.BIG_ENDIAN);
        assert offset == (int) offset;
        this.name = name;
    }

    @Override
    public PosixPointer add(int off) {
        // realSize always holds the total buffer size regardless of the offset
        return new ByteBufferPosixPointer(buffer, offset + off, size - off, realSize, name);
    }

    @Override
    public byte getI8() {
        if (offset >= realSize) {
            return 0;
        } else {
            return buffer.get((int) offset);
        }
    }

    @Override
    public short getI16() {
        if (offset >= realSize) {
            return 0;
        } else {
            return buffer.getShort((int) offset);
        }
    }

    @Override
    public int getI32() {
        if (offset >= realSize) {
            return 0;
        } else {
            return buffer.getInt((int) offset);
        }
    }

    @Override
    public long getI64() {
        if (offset >= realSize) {
            return 0;
        } else {
            return buffer.getLong((int) offset);
        }
    }

    @Override
    public void setI8(byte val) {
        if (offset < realSize) {
            buffer.put((int) offset, val);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void setI16(short val) {
        if (offset < realSize) {
            buffer.putShort((int) offset, val);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void setI32(int val) {
        if (offset < realSize) {
            buffer.putInt((int) offset, val);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void setI64(long val) {
        if (offset < realSize) {
            buffer.putLong((int) offset, val);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public String getName() {
        return name;
    }
}
