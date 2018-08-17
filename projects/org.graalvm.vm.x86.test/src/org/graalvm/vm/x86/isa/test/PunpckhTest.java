package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Punpckh.Punpckhdq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PunpckhTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x44, 0x0f, 0x6a, (byte) 0xe0};
    private static final String ASSEMBLY1 = "punpckhdq\txmm12,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Punpckhdq.class);
    }
}
