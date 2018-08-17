package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Subpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SubpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x5c, (byte) 0xd4};
    private static final String ASSEMBLY1 = "subpd\txmm2,xmm4";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Subpd.class);
    }
}
