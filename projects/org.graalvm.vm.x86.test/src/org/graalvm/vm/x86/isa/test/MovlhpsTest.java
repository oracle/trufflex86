package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movlhps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovlhpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x16, (byte) 0xdf};
    private static final String ASSEMBLY1 = "movlhps\txmm3,xmm7";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movlhps.class);
    }
}
