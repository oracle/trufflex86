package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Padd.Paddq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PaddTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x44, 0x0f, (byte) 0xd4, (byte) 0xc9};
    private static final String ASSEMBLY1 = "paddq\txmm9,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Paddq.class);
    }
}
