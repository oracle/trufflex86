package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psra.Psrad;
import org.graalvm.vm.x86.isa.instruction.Psra.Psraw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsraTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x72, (byte) 0xe7, 0x1f};
    private static final String ASSEMBLY1 = "psrad\txmm7,0x1f";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xe2, (byte) 0xc1};
    private static final String ASSEMBLY2 = "psrad\txmm0,xmm1";

    private static final byte[] MACHINECODE3 = {0x66, 0x0f, 0x71, (byte) 0xe4, 0x01};
    private static final String ASSEMBLY3 = "psraw\txmm4,0x1";

    private static final byte[] MACHINECODE4 = {0x66, 0x0f, 0x71, (byte) 0xe5, 0x01};
    private static final String ASSEMBLY4 = "psraw\txmm5,0x1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psrad.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psrad.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Psraw.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Psraw.class);
    }
}
