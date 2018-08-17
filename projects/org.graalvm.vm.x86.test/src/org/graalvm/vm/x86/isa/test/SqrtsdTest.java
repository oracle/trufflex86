package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Sqrtsd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SqrtsdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, 0x51, (byte) 0xc9};
    private static final String ASSEMBLY1 = "sqrtsd\txmm1,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Sqrtsd.class);
    }
}
