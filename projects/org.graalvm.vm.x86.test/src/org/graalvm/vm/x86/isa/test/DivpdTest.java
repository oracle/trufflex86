package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Divpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class DivpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x5e, (byte) 0xc6};
    private static final String ASSEMBLY1 = "divpd\txmm0,xmm14";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Divpd.class);
    }
}
