package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Psrl.Psrld;
import org.graalvm.vm.x86.isa.instruction.Psrl.Psrlq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class PsrlTest {
    public static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x73, (byte) 0xd1, 0x20};
    public static final String ASSEMBLY1 = "psrlq\txmm9,0x20";

    public static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x72, (byte) 0xd0, 0x1f};
    public static final String ASSEMBLY2 = "psrld\txmm0,0x1f";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Psrlq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Psrld);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
