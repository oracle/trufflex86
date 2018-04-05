package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Fxrstor;
import org.graalvm.vm.x86.isa.instruction.Fxsave;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class FxsaveTest {
    public static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0x44, (byte) 0x24, (byte) 0x40};
    public static final String ASSEMBLY1 = "fxsave\t[rsp+0x40]";

    public static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, 0x4c, 0x24, 0x40};
    public static final String ASSEMBLY2 = "fxrstor\t[rsp+0x40]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Fxsave);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Fxrstor);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }
}
