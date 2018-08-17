package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Stmxcsr;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class StmxcsrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, 0x5c, 0x24, 0x70};
    private static final String ASSEMBLY1 = "stmxcsr\t[rsp+0x70]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Stmxcsr.class);
    }
}
