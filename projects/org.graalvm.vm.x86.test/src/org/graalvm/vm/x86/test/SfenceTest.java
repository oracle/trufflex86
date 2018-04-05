package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Sfence;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class SfenceTest {
    public static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0xf8};
    public static final String ASSEMBLY1 = "sfence";

    public static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, (byte) 0xf9};
    public static final String ASSEMBLY2 = "sfence";

    public static final byte[] MACHINECODE3 = {0x0f, (byte) 0xae, (byte) 0xfa};
    public static final String ASSEMBLY3 = "sfence";

    public static final byte[] MACHINECODE4 = {0x0f, (byte) 0xae, (byte) 0xfb};
    public static final String ASSEMBLY4 = "sfence";

    public static final byte[] MACHINECODE5 = {0x0f, (byte) 0xae, (byte) 0xfc};
    public static final String ASSEMBLY5 = "sfence";

    public static final byte[] MACHINECODE6 = {0x0f, (byte) 0xae, (byte) 0xfd};
    public static final String ASSEMBLY6 = "sfence";

    public static final byte[] MACHINECODE7 = {0x0f, (byte) 0xae, (byte) 0xfe};
    public static final String ASSEMBLY7 = "sfence";

    public static final byte[] MACHINECODE8 = {0x0f, (byte) 0xae, (byte) 0xff};
    public static final String ASSEMBLY8 = "sfence";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }

    @Test
    public void test5() {
        CodeReader reader = new CodeArrayReader(MACHINECODE5, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY5, insn.toString());
        assertEquals(MACHINECODE5.length, reader.getPC());
    }

    @Test
    public void test6() {
        CodeReader reader = new CodeArrayReader(MACHINECODE6, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY6, insn.toString());
        assertEquals(MACHINECODE6.length, reader.getPC());
    }

    @Test
    public void test7() {
        CodeReader reader = new CodeArrayReader(MACHINECODE7, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY7, insn.toString());
        assertEquals(MACHINECODE7.length, reader.getPC());
    }

    @Test
    public void test8() {
        CodeReader reader = new CodeArrayReader(MACHINECODE8, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sfence);
        assertEquals(ASSEMBLY8, insn.toString());
        assertEquals(MACHINECODE8.length, reader.getPC());
    }
}
