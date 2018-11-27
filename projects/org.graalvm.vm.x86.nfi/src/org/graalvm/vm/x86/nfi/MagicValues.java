package org.graalvm.vm.x86.nfi;

public class MagicValues {
    public static long encodeObject(int id) {
        return 0xDEADBEEF00000000L | id;
    }

    public static int decodeObject(long value) {
        return (int) value;
    }

    public static boolean isObject(long value) {
        return (int) (value >> 32) == 0xDEADBEEF;
    }
}
