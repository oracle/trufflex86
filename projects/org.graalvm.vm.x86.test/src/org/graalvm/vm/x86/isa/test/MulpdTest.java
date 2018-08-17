package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Mulpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MulpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x59, (byte) 0xc3};
    private static final String ASSEMBLY1 = "mulpd\txmm0,xmm3";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Mulpd.class);
    }
}
