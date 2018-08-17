package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Bt.Btq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class BtTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, 0x0f, (byte) 0xba, (byte) 0xe2, 0x2d};
    private static final String ASSEMBLY1 = "bt\trdx,0x2d";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Btq.class);
    }
}
