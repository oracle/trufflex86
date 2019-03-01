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
package org.graalvm.vm.x86.isa;

import org.graalvm.vm.util.HexFormatter;

public abstract class CodeReader {
    public abstract byte read8();

    public abstract byte peek8(int offset);

    public boolean isAvailable() {
        return false;
    }

    public abstract long getPC();

    public void setPC(@SuppressWarnings("unused") long pc) {
        throw new AssertionError("not implemented");
    }

    public short read16() {
        byte a = read8();
        byte b = read8();
        return (short) (Byte.toUnsignedInt(a) | (Byte.toUnsignedInt(b) << 8));
    }

    public int read32() {
        byte a = read8();
        byte b = read8();
        byte c = read8();
        byte d = read8();
        return Byte.toUnsignedInt(a) | (Byte.toUnsignedInt(b) << 8) | (Byte.toUnsignedInt(c) << 16) | (Byte.toUnsignedInt(d) << 24);
    }

    public long read64() {
        byte a = read8();
        byte b = read8();
        byte c = read8();
        byte d = read8();
        byte e = read8();
        byte f = read8();
        byte g = read8();
        byte h = read8();
        return Byte.toUnsignedLong(a) | (Byte.toUnsignedLong(b) << 8) | (Byte.toUnsignedLong(c) << 16) | (Byte.toUnsignedLong(d) << 24) | (Byte.toUnsignedLong(e) << 32) |
                        (Byte.toUnsignedLong(f) << 40) | (Byte.toUnsignedLong(g) << 48) | (Byte.toUnsignedLong(h) << 56);
    }

    public void check(byte[] ref) {
        for (int i = 0; i < ref.length; i++) {
            byte act = read8();
            if (ref[i] != act) {
                throw new AssertionError("data mismatch at 0x" + HexFormatter.tohex(getPC(), 16));
            }
        }
    }
}
