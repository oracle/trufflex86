package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Comisd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class ComisdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x2f, (byte) 0xc1};
    private static final String ASSEMBLY1 = "comisd\txmm0,xmm9";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Comisd.class);
    }
}
