package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pmulhuw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PmulhuwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xe4, 0x05, 0x7a, 0x0e, 0x00, 0x00};
    private static final String ASSEMBLY1 = "pmulhuw\txmm0,[rip+0xe7a]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pmulhuw.class);
    }
}
