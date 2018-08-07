package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Mfence;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class MfenceTest {
    public static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0xf0};
    public static final String ASSEMBLY1 = "mfence";

    public static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, (byte) 0xf1};
    public static final String ASSEMBLY2 = "mfence";

    public static final byte[] MACHINECODE3 = {0x0f, (byte) 0xae, (byte) 0xf2};
    public static final String ASSEMBLY3 = "mfence";

    public static final byte[] MACHINECODE4 = {0x0f, (byte) 0xae, (byte) 0xf3};
    public static final String ASSEMBLY4 = "mfence";

    public static final byte[] MACHINECODE5 = {0x0f, (byte) 0xae, (byte) 0xf4};
    public static final String ASSEMBLY5 = "mfence";

    public static final byte[] MACHINECODE6 = {0x0f, (byte) 0xae, (byte) 0xf5};
    public static final String ASSEMBLY6 = "mfence";

    public static final byte[] MACHINECODE7 = {0x0f, (byte) 0xae, (byte) 0xf6};
    public static final String ASSEMBLY7 = "mfence";

    public static final byte[] MACHINECODE8 = {0x0f, (byte) 0xae, (byte) 0xf7};
    public static final String ASSEMBLY8 = "mfence";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }

    @Test
    public void test5() {
        CodeReader reader = new CodeArrayReader(MACHINECODE5, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY5, insn.toString());
        assertEquals(MACHINECODE5.length, reader.getPC());
    }

    @Test
    public void test6() {
        CodeReader reader = new CodeArrayReader(MACHINECODE6, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY6, insn.toString());
        assertEquals(MACHINECODE6.length, reader.getPC());
    }

    @Test
    public void test7() {
        CodeReader reader = new CodeArrayReader(MACHINECODE7, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY7, insn.toString());
        assertEquals(MACHINECODE7.length, reader.getPC());
    }

    @Test
    public void test8() {
        CodeReader reader = new CodeArrayReader(MACHINECODE8, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mfence);
        assertEquals(ASSEMBLY8, insn.toString());
        assertEquals(MACHINECODE8.length, reader.getPC());
    }
}
