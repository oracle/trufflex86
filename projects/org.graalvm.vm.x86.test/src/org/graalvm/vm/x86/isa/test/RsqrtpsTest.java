package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Rsqrtps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class RsqrtpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0x0f, 0x52, (byte) 0xd2};
    private static final String ASSEMBLY1 = "rsqrtps\txmm2,xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Rsqrtps.class);
    }
}
