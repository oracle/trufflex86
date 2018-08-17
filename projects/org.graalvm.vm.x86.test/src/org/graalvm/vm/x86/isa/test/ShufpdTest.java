package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Shufpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class ShufpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xc6, (byte) 0xff, 0x01};
    private static final String ASSEMBLY1 = "shufpd\txmm7,xmm7,0x1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shufpd.class);
    }
}
