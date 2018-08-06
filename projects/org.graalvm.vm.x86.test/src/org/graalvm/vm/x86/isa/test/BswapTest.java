package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Bswap.Bswapl;
import org.graalvm.vm.x86.isa.instruction.Bswap.Bswapq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class BswapTest {
    public static final byte[] MACHINECODE1 = {0x0f, (byte) 0xc8};
    public static final String ASSEMBLY1 = "bswap\teax";

    public static final byte[] MACHINECODE2 = {0x48, 0x0f, (byte) 0xca};
    public static final String ASSEMBLY2 = "bswap\trdx";

    public static final byte[] MACHINECODE3 = {0x41, 0x0f, (byte) 0xc9};
    public static final String ASSEMBLY3 = "bswap\tr9d";

    public static final byte[] MACHINECODE4 = {0x41, 0x0f, (byte) 0xcf};
    public static final String ASSEMBLY4 = "bswap\tr15d";

    public static final byte[] MACHINECODE5 = {0x49, 0x0f, (byte) 0xcf};
    public static final String ASSEMBLY5 = "bswap\tr15";

    public static final byte[] MACHINECODE6 = {0x0f, (byte) 0xc9};
    public static final String ASSEMBLY6 = "bswap\tecx";

    public static final byte[] MACHINECODE7 = {0x48, 0x0f, (byte) 0xc9};
    public static final String ASSEMBLY7 = "bswap\trcx";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapl);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapq);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapl);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapl);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }

    @Test
    public void test5() {
        CodeReader reader = new CodeArrayReader(MACHINECODE5, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapq);
        assertEquals(ASSEMBLY5, insn.toString());
        assertEquals(MACHINECODE5.length, reader.getPC());
    }

    @Test
    public void test6() {
        CodeReader reader = new CodeArrayReader(MACHINECODE6, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapl);
        assertEquals(ASSEMBLY6, insn.toString());
        assertEquals(MACHINECODE6.length, reader.getPC());
    }

    @Test
    public void test7() {
        CodeReader reader = new CodeArrayReader(MACHINECODE7, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Bswapq);
        assertEquals(ASSEMBLY7, insn.toString());
        assertEquals(MACHINECODE7.length, reader.getPC());
    }
}
