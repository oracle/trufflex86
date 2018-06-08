package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Punpckl.Punpckldq;
import org.graalvm.vm.x86.isa.instruction.Punpckl.Punpcklqdq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class PunpcklTest {
    public static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x62, (byte) 0xc1};
    public static final String ASSEMBLY1 = "punpckldq\txmm0,xmm9";

    public static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x6c, (byte) 0xc0};
    public static final String ASSEMBLY2 = "punpcklqdq\txmm0,xmm0";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Punpckldq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Punpcklqdq);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
