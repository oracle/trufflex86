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
package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.PosixPointer;

public class CallbackCode {
    public static final long DATA_OFFSET = 3584;

    // @formatter:off
    private static final byte[] CALLBACK = {
            /* 00 */  0x49, (byte) 0xba, 0x00, 0x00, 0x00, 0x00, 0x00,  // movabs r10,0x0000000000000000
            /* 07 */  0x00, 0x00, 0x00,
            /* 0a */  0x41, (byte) 0x8a, (byte) 0x42, (byte) 0x02,      // mov    al,BYTE PTR [r10+0x2]
            /* 0e */  (byte) 0x84, (byte) 0xc0,                         // test   al,al
            /* 10 */  0x75, 0x03,                                       // jne    15 <loc+0x15>
            /* 12 */  (byte) 0xf8,                                      // clc
            /* 13 */  (byte) 0xeb, 0x01,                                // jmp    16 <loc+0x16>
            /* 15 */  (byte) 0xf9,                                      // stc
            /* 16 */  0x66, 0x41, (byte) 0x8b, 0x02,                    // mov    ax,WORD PTR [r10]
            /* 1a */  (byte) 0xf1,                                      // icebp
            /* 1b */  (byte) 0xc3                                       // ret
    };
    // @formatter:on

    public static long getCallbackAddress(long ptr, int id) {
        return ptr + id * CALLBACK.length;
    }

    public static long getCallbackDataAddress(long ptr, int id) {
        return ptr + DATA_OFFSET + id * 4;
    }

    public static PosixPointer getCallbackDataPointer(PosixPointer ptr, int id) {
        return ptr.add((int) (DATA_OFFSET + id * 4));
    }

    public static void writeCallback(VirtualMemory mem, long ptr, int id) {
        long base = getCallbackAddress(ptr, id);
        long data = getCallbackDataAddress(ptr, id);
        for (int i = 0; i < 2; i++) {
            mem.setI8(base + i, CALLBACK[i]);
        }
        for (int i = 10; i < CALLBACK.length; i++) {
            mem.setI8(base + i, CALLBACK[i]);
        }
        mem.setI64(base + 2, data);
    }
}
