package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class TestTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x45, (byte) 0x84, (byte) 0xe4};
    private static final String ASSEMBLY1 = "test\tr12b,r12b";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, org.graalvm.vm.x86.isa.instruction.Test.class);
    }
}
