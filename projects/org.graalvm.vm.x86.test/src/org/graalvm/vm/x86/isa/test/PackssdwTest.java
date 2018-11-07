package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Packssdw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PackssdwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x6b, (byte) 0xef};
    private static final String ASSEMBLY1 = "packssdw\txmm5,xmm7";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Packssdw.class);
    }
}
