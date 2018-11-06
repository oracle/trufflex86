package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pinsrw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PinsrwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, (byte) 0xc4, (byte) 0xc4, 0x05};
    private static final String ASSEMBLY1 = "pinsrw\txmm0,r12d,0x5";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xc4, (byte) 0xc5, 0x06};
    private static final String ASSEMBLY2 = "pinsrw\txmm0,ebp,0x6";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pinsrw.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Pinsrw.class);
    }
}
