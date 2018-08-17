package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x11, 0x14, (byte) 0x85, 0x0c, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY1 = "movss\t[rax*4+0xc],xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movss.class);
    }
}
