package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Sqrtsd;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class SqrtsdTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, 0x51, (byte) 0xc9};
    public static final String ASSEMBLY1 = "sqrtsd\txmm1,xmm1";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sqrtsd);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
