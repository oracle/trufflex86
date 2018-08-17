package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Andnps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AndnpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x44, 0x0f, 0x55, (byte) 0xf9};
    private static final String ASSEMBLY1 = "andnps\txmm15,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Andnps.class);
    }
}
