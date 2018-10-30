package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pmullw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PmullwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xd5, 0x0d, (byte) 0xfc, 0x0f, 0x00, 0x00};
    private static final String ASSEMBLY1 = "pmullw\txmm1,[rip+0xffc]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pmullw.class);
    }
}
