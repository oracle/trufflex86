package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Pmuludq;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class PmuludqTest {
    public static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, (byte) 0xf4, (byte) 0xc2};
    public static final String ASSEMBLY1 = "pmuludq\txmm0,xmm10";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Pmuludq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
