package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psubus.Psubusw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsubusTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xd9, (byte) 0xc1};
    private static final String ASSEMBLY1 = "psubusw\txmm0,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psubusw.class);
    }
}
