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
import org.graalvm.vm.posix.api.PosixPointer;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class PosixMemory extends Memory {
    private final PosixPointer ptr;
    private final boolean readonly;

    public PosixMemory(PosixPointer ptr, boolean isBE, boolean readonly) {
        super(isBE);
        this.ptr = ptr;
        this.readonly = readonly;
    }

    public boolean isReadOnly() {
        return readonly;
    }

    private PosixPointer ptr(long pos) {
        assert pos == (int) pos : String.format("0x%016X vs 0x%016X", pos, (int) pos);
        return ptr.add((int) pos);
    }

    @TruffleBoundary
    @Override
    protected byte i8(long pos) {
        try {
            return ptr(pos).getI8();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected short i16L(long pos) {
        try {
            return Short.reverseBytes(ptr(pos).getI16());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected short i16B(long pos) {
        try {
            return ptr(pos).getI16();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected int i32L(long pos) {
        try {
            return Integer.reverseBytes(ptr(pos).getI32());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected int i32B(long pos) {
        try {
            return ptr(pos).getI32();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected long i64L(long pos) {
        try {
            return Long.reverseBytes(ptr(pos).getI64());
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected long i64B(long pos) {
        try {
            return ptr(pos).getI64();
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i8(long pos, byte val) {
        try {
            ptr(pos).setI8(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i16L(long pos, short val) {
        try {
            ptr(pos).setI16(Short.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i16B(long pos, short val) {
        try {
            ptr(pos).setI16(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i32L(long pos, int val) {
        try {
            ptr(pos).setI32(Integer.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i32B(long pos, int val) {
        try {
            ptr(pos).setI32(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i64L(long pos, long val) {
        try {
            ptr(pos).setI64(Long.reverseBytes(val));
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    protected void i64B(long pos, long val) {
        try {
            ptr(pos).setI64(val);
        } catch (RuntimeException e) {
            throw new SegmentationViolation(this, pos);
        }
    }

    @TruffleBoundary
    @Override
    public long size() {
        return ptr.size();
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        return "[" + (readonly ? "ro" : "rw") + ":" + ptr.toString() + "]";
    }
}
