package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Ror.Rorl;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class RorTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xd3, (byte) 0xce};
    private static final String ASSEMBLY1 = "ror\tesi,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Rorl.class);
    }
}
