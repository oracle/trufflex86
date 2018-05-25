package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Cmpsd.Cmpnlesd;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class CmpsdTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, (byte) 0xc2, (byte) 0xf0, 0x06};
    public static final String ASSEMBLY1 = "cmpnlesd\txmm6,xmm0";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Cmpnlesd);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
