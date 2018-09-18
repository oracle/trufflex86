package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Shr.Shrb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class ShrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, (byte) 0xd2, (byte) 0xe9};
    private static final String ASSEMBLY1 = "shr\tr9b,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shrb.class);
    }
}
