package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Emms;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class EmmsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x77};
    private static final String ASSEMBLY1 = "emms";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Emms.class);
    }
}
