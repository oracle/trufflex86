package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Xorps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class XorpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x57, 0x05, 0x10, 0x1e, 0x0f, 0x00};
    private static final String ASSEMBLY1 = "xorps\txmm0,[rip+0xf1e10]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Xorps.class);
    }
}
