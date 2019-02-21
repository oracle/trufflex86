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

import org.graalvm.vm.util.io.Endianess;

public class BytePosixPointer implements PosixPointer {
    public final byte[] memory;
    public final int offset;
    private final long address;

    public BytePosixPointer(byte[] memory) {
        this(memory, 0, -1);
    }

    public BytePosixPointer(byte[] memory, int offset) {
        this(memory, offset, -1);
    }

    public BytePosixPointer(byte[] memory, int offset, long address) {
        this.memory = memory;
        this.offset = offset;
        this.address = address;
    }

    @Override
    public PosixPointer add(int off) {
        return new BytePosixPointer(memory, offset + off);
    }

    @Override
    public byte getI8() {
        try {
            return memory[offset];
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI8(byte val) {
        try {
            memory[offset] = val;
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public short getI16() {
        try {
            return Endianess.get16bitBE(memory, offset);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI16(short val) {
        try {
            Endianess.set16bitBE(memory, offset, val);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public int getI32() {
        try {
            return Endianess.get32bitBE(memory, offset);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI32(int val) {
        try {
            Endianess.set32bitBE(memory, offset, val);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public long getI64() {
        try {
            return Endianess.get64bitBE(memory, offset);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public void setI64(long val) {
        try {
            Endianess.set64bitBE(memory, offset, val);
        } catch (IndexOutOfBoundsException e) {
            throw new MemoryFaultException(e);
        }
    }

    @Override
    public boolean hasMemory(int size) {
        return size() >= size;
    }

    @Override
    public byte[] getMemory() {
        return memory;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public long size() {
        return memory.length - offset;
    }

    @Override
    public int hashCode() {
        return (int) (memory.length + offset + address);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BytePosixPointer)) {
            return false;
        }
        BytePosixPointer p = (BytePosixPointer) o;
        assert (memory != p.memory || offset != p.offset || address == p.address);
        return memory == p.memory && offset == p.offset;
    }

    @Override
    public String toString() {
        if (address != -1) {
            return String.format("PTR[0x%x]", address);
        } else {
            return String.format("PTR[%s@%d]", System.identityHashCode(memory), offset);
        }
    }
}
