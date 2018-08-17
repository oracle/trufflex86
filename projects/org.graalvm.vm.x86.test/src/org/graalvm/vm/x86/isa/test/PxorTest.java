package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pxor;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PxorTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x45, 0x0f, (byte) 0xef, (byte) 0xd2};
    private static final String ASSEMBLY1 = "pxor\txmm10,xmm10";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pxor.class);
    }
}
