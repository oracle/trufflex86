package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Maxss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MaxssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x5f, (byte) 0xe0};
    private static final String ASSEMBLY1 = "maxss\txmm4,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Maxss.class);
    }
}
