package org.graalvm.vm.x86.util;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class HexFormatter {
    @ExplodeLoop
    public static String tohex(long val, int len) {
        CompilerAsserts.partialEvaluationConstant(len);
        String hex = Long.toHexString(val);
        if (len == 8) {
            String zeroPad = "00000000";
            return zeroPad.substring(hex.length()) + hex;
        } else if (len == 16) {
            String zeroPad = "0000000000000000";
            return zeroPad.substring(hex.length()) + hex;
        }
        StringBuilder buf = new StringBuilder(len);
        for (int i = hex.length(); i < len; i++) {
            buf.append('0');
        }
        return buf.append(hex).toString();
    }
}
