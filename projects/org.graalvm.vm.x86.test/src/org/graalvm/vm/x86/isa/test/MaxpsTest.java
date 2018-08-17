package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Maxps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MaxpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x44, 0x0f, 0x5f, (byte) 0xe8};
    private static final String ASSEMBLY1 = "maxps\txmm13,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Maxps.class);
    }
}
