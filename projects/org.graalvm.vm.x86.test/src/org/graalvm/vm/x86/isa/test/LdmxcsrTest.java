package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Ldmxcsr;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class LdmxcsrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, 0x54, 0x24, 0x04};
    private static final String ASSEMBLY1 = "ldmxcsr\t[rsp+0x4]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Ldmxcsr.class);
    }
}
