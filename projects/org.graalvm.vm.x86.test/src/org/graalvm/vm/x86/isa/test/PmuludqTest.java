package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pmuludq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PmuludqTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, (byte) 0xf4, (byte) 0xc2};
    private static final String ASSEMBLY1 = "pmuludq\txmm0,xmm10";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pmuludq.class);
    }
}
