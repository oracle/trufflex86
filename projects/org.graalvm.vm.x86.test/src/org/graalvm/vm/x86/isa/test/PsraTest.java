package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psra.Psrad;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsraTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x72, (byte) 0xe7, 0x1f};
    private static final String ASSEMBLY1 = "psrad\txmm7,0x1f";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xe2, (byte) 0xc1};
    private static final String ASSEMBLY2 = "psrad\txmm0,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psrad.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psrad.class);
    }
}
