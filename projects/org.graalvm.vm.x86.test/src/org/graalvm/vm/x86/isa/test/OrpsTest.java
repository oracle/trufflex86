package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Orps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class OrpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, 0x0f, 0x56, (byte) 0xc7};
    private static final String ASSEMBLY1 = "orps\txmm0,xmm15";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Orps.class);
    }
}
