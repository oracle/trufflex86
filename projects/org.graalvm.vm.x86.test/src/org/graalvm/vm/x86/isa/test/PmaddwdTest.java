package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pmaddwd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PmaddwdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xf5, 0x25, (byte) 0xe8, (byte) 0xd7, 0x03, 0x00};
    private static final String ASSEMBLY1 = "pmaddwd\txmm4,[rip+0x3d7e8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pmaddwd.class);
    }
}
