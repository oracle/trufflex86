package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Lea;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class LeaTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, (byte) 0x8d, 0x04, (byte) 0xfd, 0x00, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY1 = "lea\trax,[rdi*8]";

    private static final byte[] MACHINECODE2 = {0x49, (byte) 0x8d, 0x74, 0x24, 0x01};
    private static final String ASSEMBLY2 = "lea\trsi,[r12+0x1]";

    private static final byte[] MACHINECODE3 = {0x4b, (byte) 0x8d, 0x74, 0x35, 0x00};
    private static final String ASSEMBLY3 = "lea\trsi,[r13+r14]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Lea.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Lea.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Lea.class);
    }
}
