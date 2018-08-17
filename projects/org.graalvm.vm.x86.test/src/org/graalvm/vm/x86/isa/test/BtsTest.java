package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Bts.Btsq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class BtsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, 0x0f, (byte) 0xba, (byte) 0xee, 0x34};
    private static final String ASSEMBLY1 = "bts\trsi,0x34";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Btsq.class);
    }
}
