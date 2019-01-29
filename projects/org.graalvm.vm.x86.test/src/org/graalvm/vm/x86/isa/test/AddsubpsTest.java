package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Addsubps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AddsubpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, (byte) 0xd0, (byte) 0xc1};
    private static final String ASSEMBLY1 = "addsubps\txmm0,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Addsubps.class);
    }
}
