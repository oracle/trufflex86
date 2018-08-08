package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Adc.Adcb;
import org.graalvm.vm.x86.isa.instruction.Adc.Adcl;
import org.graalvm.vm.x86.isa.instruction.Adc.Adcq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class AdcTest {
    public static final byte[] MACHINECODE1 = {(byte) 0x83, (byte) 0xd0, (byte) 0xff};
    public static final String ASSEMBLY1 = "adc\teax,-0x1";

    public static final byte[] MACHINECODE2 = {0x41, 0x11, (byte) 0xf8};
    public static final String ASSEMBLY2 = "adc\tr8d,edi";

    public static final byte[] MACHINECODE3 = {0x4c, 0x13, 0x02};
    public static final String ASSEMBLY3 = "adc\tr8,[rdx]";

    public static final byte[] MACHINECODE4 = {0x14, (byte) 0xff};
    public static final String ASSEMBLY4 = "adc\tal,-0x1";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Adcl);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Adcl);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Adcq);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Adcb);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }
}
