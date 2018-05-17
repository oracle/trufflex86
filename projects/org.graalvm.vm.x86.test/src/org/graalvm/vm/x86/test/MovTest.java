package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Mov;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class MovTest {
    public static final byte[] MACHINECODE1 = {0x48, (byte) 0xb9, (byte) 0xcd, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc};
    public static final String ASSEMBLY1 = "movabs\trcx,0xcccccccccccccccd";

    public static final byte[] MACHINECODE2 = {0x48, (byte) 0x8b, 0x04, 0x24};
    public static final String ASSEMBLY2 = "mov\trax,[rsp]";

    public static final byte[] MACHINECODE3 = {(byte) 0x8a, 0x06};
    public static final String ASSEMBLY3 = "mov\tal,[rsi]";

    public static final byte[] MACHINECODE4 = {0x49, (byte) 0xb9, (byte) 0xd0, 0x03, 0x00, (byte) 0x80, 0x03, 0x00, 0x00, 0x00};
    public static final String ASSEMBLY4 = "movabs\tr9,0x3800003d0";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mov);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mov);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Mov);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Movq);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }
}
