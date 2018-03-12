package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Call.CallAbsolute;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.isa.instruction.Movsxd;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.junit.Test;

public class ModRMTest {
    public static final byte[] MACHINECODE1 = {0x64, 0x48, (byte) 0x89, 0x04, 0x25, 0x28, 0x00, 0x00, 0x00};
    public static final String ASSEMBLY1 = "mov\tfs:[0x28],rax";

    public static final byte[] MACHINECODE2 = {(byte) 0xff, 0x54, (byte) 0xdd, 0x00};
    public static final String ASSEMBLY2 = "call\t[rbp+rbx*8]";

    public static final byte[] MACHINECODE3 = {0x4a, 0x63, 0x04, (byte) 0xa2};
    public static final String ASSEMBLY3 = "movsxd\trax,[rdx+r12*4]";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Movq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void test2() {
        CodeReader reader = new CodeArrayReader(MACHINECODE2, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof CallAbsolute);
        assertEquals(ASSEMBLY2, insn.toString());
        assertEquals(MACHINECODE2.length, reader.getPC());
    }

    @Test
    public void test3() {
        CodeReader reader = new CodeArrayReader(MACHINECODE3, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Movsxd);
        assertEquals(ASSEMBLY3, insn.toString());
        assertEquals(MACHINECODE3.length, reader.getPC());
    }
}
