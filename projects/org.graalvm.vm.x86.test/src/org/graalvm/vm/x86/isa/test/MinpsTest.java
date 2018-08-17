package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Minps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MinpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x5d, (byte) 0xc8};
    private static final String ASSEMBLY1 = "minps\txmm1,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Minps.class);
    }
}
