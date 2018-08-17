package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Minss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MinssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x5d, (byte) 0xc1};
    private static final String ASSEMBLY1 = "minss\txmm0,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Minss.class);
    }
}
