package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movhlps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovhlpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x12, (byte) 0xc8};
    private static final String ASSEMBLY1 = "movhlps\txmm1,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movhlps.class);
    }
}
