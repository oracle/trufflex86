package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Tzcnt.Tzcntq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class TzcntTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x4d, 0x0f, (byte) 0xbc, 0x03};
    public static final String ASSEMBLY1 = "tzcnt\tr8,[r11]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Tzcntq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
