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
package org.graalvm.vm.x86;

public class AMD64 {
    public static final long STACK_SIZE = 8 * 1024 * 1024; // 8M
    public static final long STACK_ADDRESS_DBG = 0x7fff6c845000L;
    public static final long STACK_ADDRESS_NATIVE = 0x1ffffffff000L;
    // public static final long STACK_ADDRESS = 0xf6fff000L;
    // public static final long STACK_ADDRESS = 0x0000800000000000L;
    public static final long STACK_ADDRESS = Options.getBoolean(Options.DEBUG_STATIC_ENV) ? STACK_ADDRESS_DBG : STACK_ADDRESS_NATIVE;
    public static final long STACK_BASE = STACK_ADDRESS - STACK_SIZE;

    public static final int DCACHE_LINE_SIZE = 0x20;
    public static final int ICACHE_LINE_SIZE = 0x20;

    public static final long SCRATCH_SIZE = 4 * 1024 * 1024; // 4MB

    public static final long RETURN_BASE = STACK_BASE - 16384;

    // @formatter:off
    public static final byte[] RETURN_CODE = {
                    0x48, (byte) 0x89, (byte) 0xc7,                     // mov    rdi,rax
                    (byte) 0xb8, 0x02, 0x00, (byte) 0xde, (byte) 0xc0,  // mov    eax,0xc0de0002
                    0x0f, 0x05,                                         // syscall
    };
    // @formatter:on
}
