package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Btr.Btrq;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.junit.Test;

public class BtrTest {
    public static final byte[] MACHINECODE1 = {0x48, 0x0f, (byte) 0xba, (byte) 0xf0, 0x3f};
    public static final String ASSEMBLY1 = "btr\trax,0x3f";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Btrq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
