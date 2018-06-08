package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Fnstcw;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class FnstcwTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xd9, (byte) 0xbd, 0x5a, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    public static final String ASSEMBLY1 = "fnstcw\t[rbp-0xa6]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Fnstcw);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
