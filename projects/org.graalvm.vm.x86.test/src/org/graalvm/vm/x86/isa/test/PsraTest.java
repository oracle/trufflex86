package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psra.Psrad;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsraTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x72, (byte) 0xe7, 0x1f};
    private static final String ASSEMBLY1 = "psrad\txmm7,0x1f";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psrad.class);
    }
}
