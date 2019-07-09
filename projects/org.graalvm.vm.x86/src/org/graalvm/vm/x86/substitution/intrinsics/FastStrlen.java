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
