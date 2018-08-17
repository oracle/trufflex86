package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pandn;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PandnTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xdf, (byte) 0xcb};
    private static final String ASSEMBLY1 = "pandn\txmm1,xmm3";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pandn.class);
    }
}
