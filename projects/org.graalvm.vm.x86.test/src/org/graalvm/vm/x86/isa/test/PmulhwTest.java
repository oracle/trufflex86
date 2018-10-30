package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pmulhw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PmulhwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xe5, 0x05, (byte) 0xe8, 0x0f, 0x00, 0x00};
    private static final String ASSEMBLY1 = "pmulhw\txmm0,[rip+0xfe8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pmulhw.class);
    }
}
