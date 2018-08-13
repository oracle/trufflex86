package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Endbr32;
import org.graalvm.vm.x86.isa.instruction.Endbr64;
import org.graalvm.vm.x86.isa.instruction.Rdssp.Rdsspq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class CetTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x1e, (byte) 0xfa};
    public static final String ASSEMBLY1 = "endbr64";

    public static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, 0x1e, (byte) 0xfb};
    public static final String ASSEMBLY2 = "endbr32";

    public static final byte[] MACHINECODE3 = {(byte) 0xf3, 0x48, 0x0f, 0x1e, (byte) 0xc8};
    public static final String ASSEMBLY3 = "rdsspq\trax";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Endbr64);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Endbr32);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Rdsspq);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }
}
