package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorl;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class XorTest {
    public static final byte[] MACHINECODE1 = {(byte) 0x83, (byte) 0xf0, 0x01};
    public static final String ASSEMBLY1 = "xor\teax,0x1";

    public static final byte[] MACHINECODE2 = {0x34, (byte) 0xff};
    public static final String ASSEMBLY2 = "xor\tal,0xff";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorl);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorb);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
