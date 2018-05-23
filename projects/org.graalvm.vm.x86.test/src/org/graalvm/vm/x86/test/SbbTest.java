package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbb;
import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbl;
import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbq;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class SbbTest {
    public static final byte[] MACHINECODE1 = {0x19, (byte) 0xc0};
    public static final String ASSEMBLY1 = "sbb\teax,eax";

    public static final byte[] MACHINECODE2 = {0x4c, 0x1b, 0x02};
    public static final String ASSEMBLY2 = "sbb\tr8,[rdx]";

    public static final byte[] MACHINECODE3 = {0x1c, 0x00};
    public static final String ASSEMBLY3 = "sbb\tal,0x0";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sbbl);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sbbq);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Sbbb);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }
}
