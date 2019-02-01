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

import java.util.logging.Logger;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class MemoryPage {
    private static final Logger log = Trace.create(MemoryPage.class);

    public final Memory memory;
    public final long base;
    public final long size;
    public final long end;
    private final long offset;

    public boolean r;
    public boolean w;
    public boolean x;

    public final String name;
    public final long fileOffset;

    private final long id = nextID();
    private static long seq = 0;

    private final static long nextID() {
        return seq++;
    }

    public MemoryPage(Memory memory, long base, long size) {
        this.memory = memory;
        this.base = base;
        this.size = size;
        this.end = base + size;
        offset = 0;
        r = true;
        w = true;
        x = true;
        name = null;
        fileOffset = 0;
    }

    public MemoryPage(Memory memory, long base, long size, String name) {
        this.memory = memory;
        this.base = base;
        this.size = size;
        this.end = base + size;
        this.name = name;
        offset = 0;
        r = true;
        w = true;
        x = true;
        fileOffset = 0;
    }

    public MemoryPage(Memory memory, long base, long size, String name, long fileOffset) {
        this.memory = memory;
        this.base = base;
        this.size = size;
        this.end = base + size;
        this.name = name;
        offset = 0;
        r = true;
        w = true;
        x = true;
        this.fileOffset = fileOffset;
    }

    protected MemoryPage(MemoryPage page) {
        this.memory = page.memory;
        this.base = page.base;
        this.size = page.size;
        this.end = page.end;
        this.offset = page.offset;
        this.r = page.r;
        this.w = page.w;
        this.x = page.x;
        this.name = page.name;
        this.fileOffset = page.fileOffset;
    }

    public MemoryPage(MemoryPage page, long address, long size) {
        this.memory = page.memory;
        this.base = address;
        this.size = size;
        this.end = base + size;
        this.r = page.r;
        this.w = page.w;
        this.x = page.x;
        this.offset = page.offset + address - page.base;
        this.name = page.name;
        this.fileOffset = page.fileOffset + (address - page.base);
    }

    public boolean contains(long address) {
        return Long.compareUnsigned(address, base) >= 0 && Long.compareUnsigned(address, end) < 0;
    }

    public Memory getMemory() {
        return memory;
    }

    public long getOffset(long addr) {
        return addr - base + offset;
    }

    public long getBase() {
        return base;
    }

    public long getEnd() {
        return end;
    }

    public byte getI8(long addr) {
        if (!r) {
            throw new SegmentationViolation(this, addr);
        }
        try {
            return memory.getI8(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public short getI16(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI16(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public int getI32(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI32(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public long getI64(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI64(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public Vector128 getI128(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI128(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public Vector256 getI256(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI256(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public Vector512 getI512(long addr) {
        if (!r) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.getI512(getOffset(addr));
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI8(long addr, byte val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI8(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI16(long addr, short val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI16(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI32(long addr, int val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI32(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI64(long addr, long val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI64(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI128(long addr, Vector128 val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI128(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI256(long addr, Vector256 val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI256(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public void setI512(long addr, Vector512 val) {
        if (x) {
            invalidateCodeCache(addr);
        }
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            memory.setI512(getOffset(addr), val);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    public byte[] get(long addr, long len) {
        if (!w) {
            throw new SegmentationViolation(addr);
        }
        try {
            return memory.get(getOffset(addr), len);
        } catch (SegmentationViolation e) {
            throw new SegmentationViolation(addr);
        }
    }

    @TruffleBoundary
    private static void invalidateCodeCache(long addr) {
        log.log(Levels.DEBUG, () -> String.format("Invalidate code cache: write to 0x%016x", addr));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof MemoryPage)) {
            return false;
        }
        MemoryPage p = (MemoryPage) o;
        return p.id == id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return String.format("%016x-%016x %c%c%cp %08x 00:00 0 %s",
                        base, end, r ? 'r' : '-', w ? 'w' : '-', x ? 'x' : '-', fileOffset,
                        name != null ? name : "");
    }
}
