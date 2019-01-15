package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movaps;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovapsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x64, 0x0f, 0x28, 0x0c, 0x25, 0x50, (byte) 0xfe, (byte) 0xff, (byte) 0xff};
    private static final String ASSEMBLY1 = "movaps\txmm1,fs:[-0x1b0]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movaps.class);
    }
}
