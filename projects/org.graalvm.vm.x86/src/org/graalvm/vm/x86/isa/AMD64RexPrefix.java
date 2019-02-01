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

import org.graalvm.vm.util.BitTest;

// manual p69
public class AMD64RexPrefix {
    private final byte prefix;
    public final boolean w;
    public final boolean r;
    public final boolean x;
    public final boolean b;

    public AMD64RexPrefix(byte prefix) {
        this.prefix = prefix;
        if ((prefix & 0xF0) != 0x40) {
            throw new IllegalArgumentException(String.format("not a REX prefix: %02x", Byte.toUnsignedInt(prefix)));
        }
        w = BitTest.test(prefix, 1 << 3);
        r = BitTest.test(prefix, 1 << 2);
        x = BitTest.test(prefix, 1 << 1);
        b = BitTest.test(prefix, 1 << 0);
    }

    public byte getPrefix() {
        return prefix;
    }

    public static boolean isREX(byte op) {
        return (op & 0xF0) == 0x40;
    }

    @Override
    public String toString() {
        return String.format("REX[w=%d,r=%d,x=%d,b=%d]", w ? 1 : 0, r ? 1 : 0, x ? 1 : 0, b ? 1 : 0);
    }
}
