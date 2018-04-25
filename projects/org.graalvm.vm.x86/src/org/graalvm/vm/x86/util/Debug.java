package org.graalvm.vm.x86.util;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class Debug {
    @TruffleBoundary
    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
