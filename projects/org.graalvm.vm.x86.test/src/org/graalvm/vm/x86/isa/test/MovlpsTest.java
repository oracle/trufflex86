package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movlps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovlpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x13, 0x45, (byte) 0xf8};
    private static final String ASSEMBLY1 = "movlps\t[rbp-0x8],xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movlps.class);
    }
}
