package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Unpckhps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class UnpckhpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x45, 0x0f, 0x15, (byte) 0xe3};
    private static final String ASSEMBLY1 = "unpckhps\txmm12,xmm11";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Unpckhps.class);
    }
}
