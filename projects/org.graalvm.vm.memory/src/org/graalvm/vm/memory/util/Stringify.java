package org.graalvm.vm.memory.util;

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
}
