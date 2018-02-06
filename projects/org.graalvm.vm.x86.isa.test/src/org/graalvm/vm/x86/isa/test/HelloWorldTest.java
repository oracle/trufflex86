package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.junit.Test;

public class HelloWorldTest {
    @Test
    public void test() throws Exception {
        byte[] code = ElfLoader.getCode("bin/helloworld.elf");
        assertEquals(29, code.length);

        CodeReader reader = new CodeArrayReader(code, 0);
        long pc = 0;
        while (reader.isAvailable()) {
            AMD64Instruction insn = AMD64InstructionDecoder.decode(pc, reader);
            pc += insn.getSize();
            System.out.println(insn);
        }
    }
}
