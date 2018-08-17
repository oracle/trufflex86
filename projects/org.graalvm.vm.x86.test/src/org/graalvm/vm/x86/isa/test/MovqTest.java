package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovqTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xd6, (byte) 0x84, 0x24, (byte) 0xa0, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY1 = "movq\t[rsp+0xa0],xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movq.class);
    }
}
