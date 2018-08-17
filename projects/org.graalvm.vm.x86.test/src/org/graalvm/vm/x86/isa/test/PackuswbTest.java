package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Packuswb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PackuswbTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x67, (byte) 0xe8};
    private static final String ASSEMBLY1 = "packuswb\txmm5,xmm8";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Packuswb.class);
    }
}
