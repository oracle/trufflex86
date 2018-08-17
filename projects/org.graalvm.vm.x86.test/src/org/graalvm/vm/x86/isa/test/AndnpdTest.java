package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Andnpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AndnpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x55, (byte) 0xd0};
    private static final String ASSEMBLY1 = "andnpd\txmm2,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Andnpd.class);
    }
}
