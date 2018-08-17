package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Unpcklps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class UnpcklpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, 0x0f, 0x14, (byte) 0xcb};
    private static final String ASSEMBLY1 = "unpcklps\txmm1,xmm11";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Unpcklps.class);
    }
}
