package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorl;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class XorTest {
    public static final byte[] MACHINECODE1 = {(byte) 0x83, (byte) 0xf0, 0x01};
    public static final String ASSEMBLY1 = "xor\teax,0x1";

    public static final byte[] MACHINECODE2 = {0x34, (byte) 0xff};
    public static final String ASSEMBLY2 = "xor\tal,-0x1";

    public static final byte[] MACHINECODE3 = {(byte) 0x80, (byte) 0xf2, (byte) 0xff};
    public static final String ASSEMBLY3 = "xor\tdl,-0x1";

    public static final byte[] MACHINECODE4 = {0x41, (byte) 0x81, (byte) 0xf6, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x3f};
    public static final String ASSEMBLY4 = "xor\tr14d,0x3fffffff";

    public static final byte[] MACHINECODE5 = {(byte) 0x81, (byte) 0xf6, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x3f};
    public static final String ASSEMBLY5 = "xor\tesi,0x3fffffff";

    public static final byte[] MACHINECODE6 = {0x32, 0x46, 0x1a};
    public static final String ASSEMBLY6 = "xor\tal,[rsi+0x1a]";

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

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorb);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorl);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }

    @Test
    public void test5() {
        CodeReader reader = new CodeArrayReader(MACHINECODE5, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorl);
        assertEquals(ASSEMBLY5, insn.toString());
        assertEquals(MACHINECODE5.length, reader.getPC());
    }

    @Test
    public void test6() {
        CodeReader reader = new CodeArrayReader(MACHINECODE6, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorb);
        assertEquals(ASSEMBLY6, insn.toString());
        assertEquals(MACHINECODE6.length, reader.getPC());
    }
}
