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
package org.graalvm.vm.x86.test.runner;

import java.util.Arrays;
import java.util.Base64;

import org.junit.Ignore;
import org.junit.Test;

public class SyscallTest {
    private static final String SHELLCODE_OUTPUT = "ylFerA8FuDwAAAAx/w8FSGVsb" +
                    "G8gd29ybGQhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwABA" +
                    "OgAQAAAAAAAAAAAAAAAAAAAAAAAAwACAAAQQAAAAAAAAAAAAAAAAAABA" +
                    "AAABADx/wAAAAAAAAAAAAAAAAAAAAAbAAAAAAACABgQQAAAAAAAAAAAA" +
                    "AAAAAAhAAAAAAACACEQQAAAAAAAAAAAAAAAAAAmAAAAAAACACoQQAAAA" +
                    "AAAAAAAAAAAAAAwAAAAEAACAAAQQAAAAAAAAAAAAAAAAAArAAAAEAACA" +
                    "AAgQAAAAAAAAAAAAAAAAAA3AAAAEAACAAAgQAAAAAAAAAAAAAAAAAA+A" +
                    "AAAEAACAAAgQAAAAAAAAAAAAAAAAAAAc3JjL3NoZWxsY29kZS05Ynl0Z" +
                    "S5hc20ubwBzaGVsbABleGl0AHRleHQAX19ic3Nfc3RhcnQAX2VkYXRhA" +
                    "F9lbmQAAC5zeW10YWIALnN0cnRhYgAuc2hzdHJ0YWIALm5vdGUuZ251L" +
                    "nByb3BlcnR5AC50ZXh0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGwAAA" +
                    "AcAAAACAAAAAAAAAOgAQAAAAAAA6AAAAAAAAAAgAAAAAAAAAAAAAAAAA" +
                    "AAACAAAAAAAAAAAAAAAAAAAAC4AAAABAAAABgAAAAAAAAAAEEAAAAAAA" +
                    "AAQAAAAAAAANgAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAABA" +
                    "AAAAgAAAAAAAAAAAAAAAAAAAAAAAAA4EAAAAAAAAAgBAAAAAAAABAAAA" +
                    "AcAAAAIAAAAAAAAABgAAAAAAAAACQAAAAMAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAQBEAAAAAAABDAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAA" +
                    "BEAAAADAAAAAAAAAAAAAAAAAAAAAAAAAIMRAAAAAAAANAAAAAAAAAAAA" +
                    "AAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAA=";

    private static final String SHELLCODE_ZERO = "ylFerA8FuDwAAAAx/w8FSGVsbG8" +
                    "gd29ybGQh";

    @Test
    public void id() throws Exception {
        TestRunner.run("syscall-id.elf", new String[0], "", "uid=1000, gid=1000\n", "", 0);
    }

    @Test
    public void sc0() throws Exception {
        TestRunner.run("sc0.asm.elf", new String[0], "", "", "", 14);
    }

    @Ignore
    @Test
    public void syscallRegs() throws Exception {
        TestRunner.run("syscall-regs.elf", new String[0], "", "rcx = 000000000040159a\nr11 = 0000000000000206\n", "", 0);
    }

    @Ignore
    @Test
    public void shellcode9Bytes() throws Exception {
        TestRunner.runBinary("shellcode-9byte.asm.elf", new String[0], "", SHELLCODE_OUTPUT, "", 0);
    }

    @Test
    public void shellcode9BytesZero() throws Exception {
        byte[] buf = Base64.getDecoder().decode(SHELLCODE_ZERO);
        String stdout = Base64.getEncoder().encodeToString(Arrays.copyOf(buf, 4069));
        TestRunner.runBinary("shellcode-9byte.asm.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void testWriteNoFault() throws Exception {
        byte[] buf = "Hello world!\n".getBytes();
        String stdout = Base64.getEncoder().encodeToString(Arrays.copyOf(buf, 4096));
        TestRunner.runBinary("write-long.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void testWriteStdin() throws Exception {
        TestRunner.run("write-stdin.asm.elf", new String[0], "", "this is hot shit!\n", "", 0);
    }
}
