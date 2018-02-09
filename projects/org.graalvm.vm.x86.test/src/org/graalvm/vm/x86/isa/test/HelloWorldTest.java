package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Disassembler;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.test.TestDataLoader;
import org.junit.Ignore;
import org.junit.Test;

import com.everyware.posix.elf.Elf;

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
            buf.append(insn).append('\n');
        }
        assertEquals(ref, buf.toString());
    }

    @Test
    public void testBytes() throws Exception {
        byte[] code = TestDataLoader.getCode("bin/helloworld.elf");
        assertEquals(29, code.length);

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
