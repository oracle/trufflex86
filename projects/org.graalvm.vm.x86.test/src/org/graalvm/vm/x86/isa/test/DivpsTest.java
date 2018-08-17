package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Divps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class DivpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0x5e, (byte) 0xc2};
    private static final String ASSEMBLY1 = "divps\txmm0,xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Divps.class);
    }
}
