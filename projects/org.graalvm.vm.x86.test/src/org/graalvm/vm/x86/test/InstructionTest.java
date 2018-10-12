package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;

public class InstructionTest {
    protected AMD64Instruction decode(byte[] code) {
        CodeReader reader = new CodeArrayReader(code, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertEquals(code.length, reader.getPC());
        return insn;
    }

    protected void check(byte[] code, String asm, Class<? extends AMD64Instruction> clazz) {
        AMD64Instruction insn = decode(code);
        assertTrue(clazz.isInstance(insn));
        assertEquals(asm, insn.getDisassembly());
    }
}
