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
