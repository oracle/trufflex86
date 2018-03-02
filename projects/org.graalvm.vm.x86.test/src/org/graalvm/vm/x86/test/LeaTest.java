package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Lea;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class LeaTest {
    public static final byte[] MACHINECODE1 = {0x48, (byte) 0x8d, 0x04, (byte) 0xfd, 0x00, 0x00, 0x00, 0x00};
    public static final String ASSEMBLY1 = "lea\trax,[rdi*8]";

    public static final byte[] MACHINECODE2 = {0x49, (byte) 0x8d, 0x74, 0x24, 0x01};
    public static final String ASSEMBLY2 = "lea\trsi,[r12+0x1]";

    public static final byte[] MACHINECODE3 = {0x4b, (byte) 0x8d, 0x74, 0x35, 0x00};
    public static final String ASSEMBLY3 = "lea\trsi,[r13+r14]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Lea);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Lea);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Lea);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }
}
