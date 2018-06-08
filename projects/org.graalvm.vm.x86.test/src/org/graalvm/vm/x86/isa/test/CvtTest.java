package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Cvtdq2pd;
import org.graalvm.vm.x86.isa.instruction.Cvtdq2ps;
import org.graalvm.vm.x86.isa.instruction.Cvtpd2ps;
import org.graalvm.vm.x86.isa.instruction.Cvtps2pd;
import org.graalvm.vm.x86.isa.instruction.Cvtsd2ss;
import org.graalvm.vm.x86.isa.instruction.Cvtss2sd;
import org.graalvm.vm.x86.isa.instruction.Cvttss2si;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class CvtTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, (byte) 0xe6, (byte) 0xc1};
    public static final String ASSEMBLY1 = "cvtdq2pd\txmm0,xmm1";

    public static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, 0x2c, (byte) 0xc1};
    public static final String ASSEMBLY2 = "cvttss2si\teax,xmm1";

    public static final byte[] MACHINECODE3 = {(byte) 0xf3, 0x0f, 0x5a, (byte) 0xc0};
    public static final String ASSEMBLY3 = "cvtss2sd\txmm0,xmm0";

    public static final byte[] MACHINECODE4 = {(byte) 0xf2, 0x0f, 0x5a, (byte) 0xc9};
    public static final String ASSEMBLY4 = "cvtsd2ss\txmm1,xmm1";

    public static final byte[] MACHINECODE5 = {0x0f, 0x5a, (byte) 0xd0};
    public static final String ASSEMBLY5 = "cvtps2pd\txmm2,xmm0";

    public static final byte[] MACHINECODE6 = {0x66, 0x0f, 0x5a, (byte) 0xd2};
    public static final String ASSEMBLY6 = "cvtpd2ps\txmm2,xmm2";

    public static final byte[] MACHINECODE7 = {0x0f, 0x5b, (byte) 0xe4};
    public static final String ASSEMBLY7 = "cvtdq2ps\txmm4,xmm4";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtdq2pd);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvttss2si);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtss2sd);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }

    @Test
    public void test4() {
        CodeReader reader = new CodeArrayReader(MACHINECODE4, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtsd2ss);
        assertEquals(ASSEMBLY4, insn.toString());
        assertEquals(MACHINECODE4.length, reader.getPC());
    }

    @Test
    public void test5() {
        CodeReader reader = new CodeArrayReader(MACHINECODE5, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtps2pd);
        assertEquals(ASSEMBLY5, insn.toString());
        assertEquals(MACHINECODE5.length, reader.getPC());
    }

    @Test
    public void test6() {
        CodeReader reader = new CodeArrayReader(MACHINECODE6, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtpd2ps);
        assertEquals(ASSEMBLY6, insn.toString());
        assertEquals(MACHINECODE6.length, reader.getPC());
    }

    @Test
    public void test7() {
        CodeReader reader = new CodeArrayReader(MACHINECODE7, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cvtdq2ps);
        assertEquals(ASSEMBLY7, insn.toString());
        assertEquals(MACHINECODE7.length, reader.getPC());
    }
}
