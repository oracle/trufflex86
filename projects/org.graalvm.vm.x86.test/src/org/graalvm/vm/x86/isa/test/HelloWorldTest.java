package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;

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

    @Ignore
    @Test
    public void testDisassembler() throws Exception {
        Elf elf = TestDataLoader.load("bin/helloworld.elf");
        String asm = AMD64Disassembler.disassemble(elf);
        System.out.println(asm);
    }
}
