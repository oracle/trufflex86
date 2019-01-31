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
