package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Pand;
import org.graalvm.vm.x86.isa.instruction.Pcmpgt.Pcmpgt128b;
import org.graalvm.vm.x86.isa.instruction.Pmaxub;
import org.junit.Test;

public class SseTest {
    public static final byte[] MACHINECODE_PMAXUB = {0x66, 0x0f, (byte) 0xde, (byte) 0xd8};
    public static final String ASSEMBLY_PMAXUB = "pmaxub\txmm3,xmm0";

    public static final byte[] MACHINECODE_PCMPGTB = {0x66, 0x44, 0x0f, 0x64, (byte) 0xc5};
    public static final String ASSEMBLY_PCMPGTB = "pcmpgtb\txmm8,xmm5";

    public static final byte[] MACHINECODE_PAND = {0x66, 0x45, 0x0f, (byte) 0xdb, (byte) 0xc1};
    public static final String ASSEMBLY_PAND = "pand\txmm8,xmm9";

    @Test
    public void testPmaxub() {
        CodeReader reader = new CodeArrayReader(MACHINECODE_PMAXUB, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Pmaxub);
        assertEquals(ASSEMBLY_PMAXUB, insn.toString());
        assertEquals(MACHINECODE_PMAXUB.length, reader.getPC());
    }

    @Test
    public void testPcmpgtb() {
        CodeReader reader = new CodeArrayReader(MACHINECODE_PCMPGTB, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Pcmpgt128b);
        assertEquals(ASSEMBLY_PCMPGTB, insn.toString());
        assertEquals(MACHINECODE_PCMPGTB.length, reader.getPC());
    }

    @Test
    public void testPand() {
        CodeReader reader = new CodeArrayReader(MACHINECODE_PAND, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Pand);
        assertEquals(ASSEMBLY_PAND, insn.toString());
        assertEquals(MACHINECODE_PAND.length, reader.getPC());
    }
}
