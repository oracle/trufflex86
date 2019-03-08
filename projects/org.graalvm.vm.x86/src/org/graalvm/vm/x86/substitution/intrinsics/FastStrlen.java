package org.graalvm.vm.x86.substitution.intrinsics;

import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryReadNode;

public class FastStrlen extends AMD64Node {
    @Child private MemoryReadNode memory;

    public FastStrlen(MemoryReadNode memory) {
        this.memory = memory;
    }

    // optimized algorithm for strlen using 64bit registers
    public long execute(long str) {
        long charPtr;
        for (charPtr = str; (charPtr & Long.BYTES - 1) != 0; charPtr++) {
            if (memory.executeI8(charPtr) == 0) {
                return charPtr - str;
            }
        }

        long longwordPtr = charPtr;

        long himagic = 0x8080808080808080L;
        long lomagic = 0x0101010101010101L;

        while (true) {
            long longword = memory.executeI64(longwordPtr += 8);

            if (((longword - lomagic) & ~longword & himagic) != 0) {
                long cp = (longwordPtr - 1);

                if (memory.executeI8(cp + 0) == 0) {
                    return cp - str;
                }
                if (memory.executeI8(cp + 1) == 0) {
                    return cp - str + 1;
                }
                if (memory.executeI8(cp + 2) == 0) {
                    return cp - str + 2;
                }
                if (memory.executeI8(cp + 3) == 0) {
                    return cp - str + 3;
                }
                if (memory.executeI8(cp + 4) == 0) {
                    return cp - str + 4;
                }
                if (memory.executeI8(cp + 5) == 0) {
                    return cp - str + 5;
                }
                if (memory.executeI8(cp + 6) == 0) {
                    return cp - str + 6;
                }
                if (memory.executeI8(cp + 7) == 0) {
                    return cp - str + 7;
                }
            }
        }
    }
}
