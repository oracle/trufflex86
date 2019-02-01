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
package org.graalvm.vm.memory.util;

import org.graalvm.vm.memory.vector.Vector128;

public class Stringify {
    public static boolean isPrintable(byte value) {
        return value >= 0x20 && value <= 0x7e; // ascii
    }

    public static String i8(byte value) {
        switch ((char) value) {
            case '\\':
                return "\\\\";
            case '\'':
                return "\\'";
            case '"':
                return "\\\"";
            case '\r':
                return "\\r";
            case '\n':
                return "\\n";
            case '\t':
                return "\\t";
            case '\f':
                return "\\f";
        }
        if (isPrintable(value)) {
            return Character.toString((char) value);
        } else {
            return String.format("\\x%02x", Byte.toUnsignedInt(value));
        }
    }

    public static String i16(short value) {
        byte high = (byte) (value >>> 8);
        byte low = (byte) value;
        if (!isPrintable(high) && !isPrintable(low)) {
            return null;
        }
        StringBuilder buf = new StringBuilder(2);
        buf.append(i8(high));
        buf.append(i8(low));
        return buf.toString();
    }

    public static String i32(int value) {
        int tmp = value;
        int printable = 0;
        for (int i = 0; i < 4; i++, tmp >>>= 8) {
            byte v = (byte) tmp;
            if (isPrintable(v)) {
                printable++;
            }
        }
        if (printable < 2) {
            return null;
        }
        StringBuilder buf = new StringBuilder(4);
        tmp = Integer.reverseBytes(value);
        for (int i = 0; i < 4; i++, tmp >>>= 8) {
            byte v = (byte) tmp;
            buf.append(i8(v));
        }
        return buf.toString();
    }

    public static String i64(long value) {
        long tmp = value;
        int printable = 0;
        for (int i = 0; i < 8; i++, tmp >>>= 8) {
            byte v = (byte) tmp;
            if (isPrintable(v)) {
                printable++;
            }
        }
        if (printable < 4) {
            return null;
        }
        StringBuilder buf = new StringBuilder(8);
        tmp = Long.reverseBytes(value);
        for (int i = 0; i < 8; i++, tmp >>= 8) {
            byte v = (byte) tmp;
            buf.append(i8(v));
        }
        return buf.toString();
    }

    public static String i128(Vector128 value) {
        String s1 = i64(value.getI64(0));
        String s2 = i64(value.getI64(0));
        if (s1 != null && s2 != null) {
            return s1 + s2;
        } else {
            return null;
        }
    }
}
