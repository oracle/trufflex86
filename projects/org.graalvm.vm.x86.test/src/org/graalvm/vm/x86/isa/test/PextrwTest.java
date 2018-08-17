package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pextrw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PextrwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xc5, (byte) 0xc8, 0x00};
    private static final String ASSEMBLY1 = "pextrw\tecx,xmm0,0x0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pextrw.class);
    }
}
