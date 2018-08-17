package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cmp.Cmpb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CmpTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x3a, 0x54, 0x2f, (byte) 0xff};
    private static final String ASSEMBLY1 = "cmp\tdl,[rdi+rbp-0x1]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cmpb.class);
    }
}
