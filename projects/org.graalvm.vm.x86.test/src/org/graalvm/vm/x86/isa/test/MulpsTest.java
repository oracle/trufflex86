package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Mulps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MulpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x59, (byte) 0xe0};
    private static final String ASSEMBLY1 = "mulps\txmm4,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Mulps.class);
    }
}
