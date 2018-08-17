package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Subps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SubpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x5c, (byte) 0xc2};
    private static final String ASSEMBLY1 = "subps\txmm0,xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Subps.class);
    }
}
