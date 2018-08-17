package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cmppd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CmppdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xcf, 0x02};
    private static final String ASSEMBLY1 = "cmplepd\txmm1,xmm7";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cmppd.class);
    }
}
