package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Clc;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class ClcTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf8};
    private static final String ASSEMBLY1 = "clc";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Clc.class);
    }
}
