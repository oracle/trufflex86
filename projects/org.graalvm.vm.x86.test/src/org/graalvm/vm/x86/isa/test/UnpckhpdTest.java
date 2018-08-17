package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Unpckhpd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class UnpckhpdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x45, 0x0f, 0x15, (byte) 0xf9};
    private static final String ASSEMBLY1 = "unpckhpd\txmm15,xmm9";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Unpckhpd.class);
    }
}
