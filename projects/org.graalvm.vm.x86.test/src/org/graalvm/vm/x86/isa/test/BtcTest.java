package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Btc.Btcq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class BtcTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, 0x0f, (byte) 0xba, (byte) 0xf8, 0x33};
    private static final String ASSEMBLY1 = "btc\trax,0x33";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Btcq.class);
    }
}
