package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Addps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AddpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x58, (byte) 0xe1};
    private static final String ASSEMBLY1 = "addps\txmm4,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Addps.class);
    }
}
