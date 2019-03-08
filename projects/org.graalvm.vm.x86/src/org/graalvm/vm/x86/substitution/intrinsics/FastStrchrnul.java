package org.graalvm.vm.x86.substitution.intrinsics;

import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryReadNode;

public class FastStrchrnul extends AMD64Node {
    private static final long MAGIC_BITS = Long.divideUnsigned(-1, 0xff) * 0xfe << 1 >>> 1 | 1;

    @Child private MemoryReadNode memory;

    public FastStrchrnul(MemoryReadNode memory) {
        this.memory = memory;
    }

    // optimized algorithm for strchrnul using 64bit registers
    public long execute(long s, byte c) {
        long charPtr;
        long longword;

        for (charPtr = s; (charPtr & (Long.BYTES - 1)) != 0; charPtr++) {
            byte value = memory.executeI8(charPtr);
            if (value == c || value == 0) {
                return charPtr;
            }
        }

        long longwordPtr = charPtr;

        long charmask = Byte.toUnsignedLong(c) | (Byte.toUnsignedLong(c) << 8);
        charmask |= charmask << 16;
        charmask |= charmask << 32;

        while (true) {
            longword = memory.executeI64(longwordPtr++);

            if ((((longword + MAGIC_BITS) ^ ~longword) & ~MAGIC_BITS) != 0 || ((((longword ^ charmask) + MAGIC_BITS) ^ ~(longword ^ charmask)) & ~MAGIC_BITS) != 0) {
                long cp = longwordPtr - 1;

                if (memory.executeI8(cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
                if (memory.executeI8(++cp) == c || memory.executeI8(cp) == '\0') {
                    return cp;
                }
            }
        }
    }
}
