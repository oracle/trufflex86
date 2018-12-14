package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Stc;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class StcTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf9};
    private static final String ASSEMBLY1 = "stc";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Stc.class);
    }
}
