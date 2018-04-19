package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Movss;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class MovssTest {
    public static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x11, 0x14, (byte) 0x85, 0x0c, 0x00, 0x00, 0x00};
    public static final String ASSEMBLY1 = "movss\t[rax*4+0xc],xmm2";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Movss);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
