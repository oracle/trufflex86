package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Xorps;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class XorpsTest {
    public static final byte[] MACHINECODE1 = {0x0f, 0x57, 0x05, 0x10, 0x1e, 0x0f, 0x00};
    public static final String ASSEMBLY1 = "xorps\txmm0,[rip+0xf1e10]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Xorps);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }
}
