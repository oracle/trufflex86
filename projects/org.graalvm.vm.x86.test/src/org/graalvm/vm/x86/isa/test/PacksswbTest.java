package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Packsswb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PacksswbTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x63, (byte) 0xec};
    private static final String ASSEMBLY1 = "packsswb\txmm5,xmm4";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Packsswb.class);
    }
}
