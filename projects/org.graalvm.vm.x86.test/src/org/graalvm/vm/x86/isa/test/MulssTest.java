package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Mulss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MulssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x59, (byte) 0xcb};
    private static final String ASSEMBLY1 = "mulss\txmm1,xmm3";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Mulss.class);
    }
}
