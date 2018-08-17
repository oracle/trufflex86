package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Andps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AndpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x54, (byte) 0xc7};
    private static final String ASSEMBLY1 = "andps\txmm0,xmm7";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Andps.class);
    }
}
