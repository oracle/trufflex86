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
package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.x86.isa.AMD64Disassembler;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.junit.Ignore;
import org.junit.Test;

public class HelloWorldTest {
    @Test
    public void test() throws Exception {
        byte[] code = TestDataLoader.getCode("bin/helloworld.elf");
        assertEquals(29, code.length);

        StringBuilder buf = new StringBuilder();
        String ref = "xor\teax,eax\n" +
                        "inc\teax\n" +
                        "mov\tedi,eax\n" +
                        "mov\trsi,0x6000cd\n" +
                        "mov\tedx,0xd\n" +
                        "syscall\n" +
                        "mov\teax,0x3c\n" +
                        "xor\tedi,edi\n" +
                        "syscall\n";
        CodeReader reader = new CodeArrayReader(code, 0);
        long pc = 0;
        while (reader.isAvailable()) {
            AMD64Instruction insn = AMD64InstructionDecoder.decode(pc, reader);
            pc += insn.getSize();
            buf.append(insn.getDisassembly()).append('\n');
        }
        assertEquals(ref, buf.toString());
    }

    @Test
    public void testBytes() throws Exception {
        byte[] code = TestDataLoader.getCode("bin/helloworld.elf");
        assertEquals(29, code.length);

        // @formatter:off
        byte[][] bytes = {
                        {0x31, (byte) 0xc0}, // xor eax,eax
                        {(byte) 0xff, (byte) 0xc0}, // inc eax
                        {(byte) 0x89, (byte) 0xc7}, // mov edi,eax
                        {0x48, (byte) 0xc7, (byte) 0xc6, (byte) 0xcd, 0x00, 0x60, 0x00}, // mov rsi,0x6000cd
                        {(byte) 0xba, 0x0d, 0x00, 0x00, 0x00}, // mov edx,0xd
                        {0x0f, 0x05}, // syscall
                        {(byte) 0xb8, 0x3c, 0x00, 0x00, 0x00}, // mov eax,0x3c
                        {0x31, (byte) 0xff}, // xor edi,edi
                        {0x0f, 0x05} // syscall
        };
        // @formatter:on

        CodeReader reader = new CodeArrayReader(code, 0);
        long pc = 0;
        int i = 0;
        while (reader.isAvailable()) {
            assertTrue(i < bytes.length);

            AMD64Instruction insn = AMD64InstructionDecoder.decode(pc, reader);
            pc += insn.getSize();

            assertArrayEquals(i + ": insn.getBytes()", bytes[i], insn.getBytes());
            assertEquals(i + ": insn.getSize()", bytes[i].length, insn.getSize());

            i++;
        }
    }

    @Ignore
    @Test
    public void testDisassembler() throws Exception {
        Elf elf = TestDataLoader.load("bin/helloworld.elf");
        String asm = AMD64Disassembler.disassemble(elf);
        System.out.println(asm);
    }
}
