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
package org.graalvm.vm.memory.hardware;

import java.nio.ByteOrder;

import org.graalvm.vm.util.UnsafeHolder;

import sun.misc.Unsafe;

public class NativeMemory {
    private static final Unsafe unsafe = UnsafeHolder.getUnsafe();
    private static final boolean isBE = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    public static byte i8(long addr) {
        return unsafe.getByte(addr);
    }

    public static short i16L(long addr) {
        short val = unsafe.getShort(addr);
        if (isBE) {
            return Short.reverseBytes(val);
        } else {
            return val;
        }
    }

    public static short i16B(long addr) {
        short val = unsafe.getShort(addr);
        if (isBE) {
            return val;
        } else {
            return Short.reverseBytes(val);
        }
    }

    public static int i32L(long addr) {
        int val = unsafe.getInt(addr);
        if (isBE) {
            return Integer.reverseBytes(val);
        } else {
            return val;
        }
    }

    public static int i32B(long addr) {
        int val = unsafe.getInt(addr);
        if (isBE) {
            return val;
        } else {
            return Integer.reverseBytes(val);
        }
    }

    public static long i64L(long addr) {
        long val = unsafe.getLong(addr);
        if (isBE) {
            return Long.reverseBytes(val);
        } else {
            return val;
        }
    }

    public static long i64B(long addr) {
        long val = unsafe.getLong(addr);
        if (isBE) {
            return val;
        } else {
            return Long.reverseBytes(val);
        }
    }

    public static void i8(long addr, byte val) {
        unsafe.putByte(addr, val);
    }

    public static void i16L(long addr, short val) {
        if (isBE) {
            unsafe.putShort(addr, Short.reverseBytes(val));
        } else {
            unsafe.putShort(addr, val);
        }
    }

    public static void i16B(long addr, short val) {
        if (isBE) {
            unsafe.putShort(addr, val);
        } else {
            unsafe.putShort(addr, Short.reverseBytes(val));
        }
    }

    public static void i32L(long addr, int val) {
        if (isBE) {
            unsafe.putInt(addr, Integer.reverseBytes(val));
        } else {
            unsafe.putInt(addr, val);
        }
    }

    public static void i32B(long addr, int val) {
        if (isBE) {
            unsafe.putInt(addr, val);
        } else {
            unsafe.putInt(addr, Integer.reverseBytes(val));
        }
    }

    public static void i64L(long addr, long val) {
        if (isBE) {
            unsafe.putLong(addr, Long.reverseBytes(val));
        } else {
            unsafe.putLong(addr, val);
        }
    }

    public static void i64B(long addr, long val) {
        if (isBE) {
            unsafe.putLong(addr, val);
        } else {
            unsafe.putLong(addr, Long.reverseBytes(val));
        }
    }

    public static boolean cmpxchgI8(long addr, byte expected, byte x) {
        int val = unsafe.getInt(addr);
        int v;
        int exp;
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            v = (val & 0x00FFFFFF) | (Byte.toUnsignedInt(x) << 24);
            exp = (val & 0x00FFFFFF) | (Byte.toUnsignedInt(expected) << 24);
        } else {
            v = (val & 0xFFFFFF00) | Byte.toUnsignedInt(x);
            exp = (val & 0xFFFFFF00) | Byte.toUnsignedInt(expected);
        }
        return unsafe.compareAndSwapInt(null, addr, exp, v);
    }

    public static boolean cmpxchgI16B(long addr, short expected, short x) {
        int val = unsafe.getInt(addr);
        int v;
        int exp;
        if (isBE) {
            v = (val & 0x0000FFFF) | (Short.toUnsignedInt(x) << 16);
            exp = (val & 0x0000FFFF) | (Short.toUnsignedInt(expected) << 16);
        } else {
            v = (val & 0xFFFF0000) | Short.toUnsignedInt(Short.reverseBytes(x));
            exp = (val & 0xFFFF0000) | Short.toUnsignedInt(Short.reverseBytes(expected));
        }
        return unsafe.compareAndSwapInt(null, addr, exp, v);
    }

    public static boolean cmpxchgI16L(long addr, short expected, short x) {
        int val = unsafe.getInt(addr);
        int v;
        int exp;
        if (isBE) {
            v = (val & 0x0000FFFF) | (Short.toUnsignedInt(Short.reverseBytes(x)) << 16);
            exp = (val & 0x0000FFFF) | (Short.toUnsignedInt(Short.reverseBytes(expected)) << 16);
        } else {
            v = (val & 0xFFFF0000) | Short.toUnsignedInt(x);
            exp = (val & 0xFFFF0000) | Short.toUnsignedInt(expected);
        }
        return unsafe.compareAndSwapInt(null, addr, exp, v);
    }

    public static boolean cmpxchgI32B(long addr, int expected, int x) {
        if (isBE) {
            return unsafe.compareAndSwapInt(null, addr, expected, x);
        } else {
            return unsafe.compareAndSwapInt(null, addr, Integer.reverseBytes(expected), Integer.reverseBytes(x));
        }
    }

    public static boolean cmpxchgI32L(long addr, int expected, int x) {
        if (isBE) {
            return unsafe.compareAndSwapInt(null, addr, Integer.reverseBytes(expected), Integer.reverseBytes(x));
        } else {
            return unsafe.compareAndSwapInt(null, addr, expected, x);
        }
    }

    public static boolean cmpxchgI64B(long addr, long expected, long x) {
        if (isBE) {
            return unsafe.compareAndSwapLong(null, addr, expected, x);
        } else {
            return unsafe.compareAndSwapLong(null, addr, Long.reverseBytes(expected), Long.reverseBytes(x));
        }
    }

    public static boolean cmpxchgI64L(long addr, long expected, long x) {
        if (isBE) {
            return unsafe.compareAndSwapLong(null, addr, Long.reverseBytes(expected), Long.reverseBytes(x));
        } else {
            return unsafe.compareAndSwapLong(null, addr, expected, x);
        }
    }

    public static void read(byte[] dst, int off, long addr, long len) {
        long ptr = addr;
        for (int i = 0; i < len; i++, ptr++) {
            dst[i + off] = i8(ptr);
        }
    }

    public static void write(long addr, byte[] src, int off, long len) {
        long ptr = addr;
        for (int i = 0; i < len; i++, ptr++) {
            i8(ptr, src[i + off]);
        }
    }
}
