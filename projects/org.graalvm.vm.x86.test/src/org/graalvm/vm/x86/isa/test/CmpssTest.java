package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Cmpss.Cmpltss;
import org.graalvm.vm.x86.isa.instruction.Cmpss.Cmpnltss;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class CmpssTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x44, 0x0f, (byte) 0xc2, (byte) 0xf9, 0x01};
    public static final String ASSEMBLY1 = "cmpltss\txmm15,xmm1";

    public static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, (byte) 0xc2, (byte) 0xf1, 0x05};
    public static final String ASSEMBLY2 = "cmpnltss\txmm6,xmm1";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cmpltss);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cmpnltss);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
