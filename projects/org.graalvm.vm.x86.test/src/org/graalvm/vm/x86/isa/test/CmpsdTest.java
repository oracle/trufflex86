package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cmpsd.Cmpeqsd;
import org.graalvm.vm.x86.isa.instruction.Cmpsd.Cmpnlesd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CmpsdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, (byte) 0xc2, (byte) 0xf0, 0x06};
    private static final String ASSEMBLY1 = "cmpnlesd\txmm6,xmm0";

    private static final byte[] MACHINECODE2 = {(byte) 0xf2, 0x0f, (byte) 0xc2, 0x47, 0x08, 0x00};
    private static final String ASSEMBLY2 = "cmpeqsd\txmm0,[rdi+0x8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cmpnlesd.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Cmpeqsd.class);
    }
}
