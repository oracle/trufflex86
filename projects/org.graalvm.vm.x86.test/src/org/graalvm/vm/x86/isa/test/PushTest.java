package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Push.Pushq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PushTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, 0x57};
    private static final String ASSEMBLY1 = "push\tr15";

    private static final byte[] MACHINECODE2 = {0x68, (byte) 0x1a, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY2 = "push\t0x1a";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pushq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Pushq.class);
    }
}
