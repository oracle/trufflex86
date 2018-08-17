package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movmskpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovmskpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x50, (byte) 0xc1};
    private static final String ASSEMBLY1 = "movmskpd\teax,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movmskpd.class);
    }
}
