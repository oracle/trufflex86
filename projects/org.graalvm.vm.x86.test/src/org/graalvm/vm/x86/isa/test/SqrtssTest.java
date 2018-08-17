package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Sqrtss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SqrtssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x51, (byte) 0xc0};
    private static final String ASSEMBLY1 = "sqrtss\txmm0,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Sqrtss.class);
    }
}
