package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Shufps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class ShufpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xc6, (byte) 0xe4, 0x00};
    private static final String ASSEMBLY1 = "shufps\txmm4,xmm4,0x0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shufps.class);
    }
}
