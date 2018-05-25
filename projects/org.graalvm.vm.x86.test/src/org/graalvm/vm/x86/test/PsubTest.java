package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Psub.Psubd;
import org.graalvm.vm.x86.isa.instruction.Psub.Psubq;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class PsubTest {
    public static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xfa, (byte) 0xe7};
    public static final String ASSEMBLY1 = "psubd\txmm4,xmm7";

    public static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xfb, (byte) 0xf3};
    public static final String ASSEMBLY2 = "psubq\txmm6,xmm3";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Psubd);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Psubq);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
