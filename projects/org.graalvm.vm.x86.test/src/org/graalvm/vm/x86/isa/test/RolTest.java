package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Rol.Rolq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class RolTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x49, (byte) 0xd3, (byte) 0xc0};
    private static final String ASSEMBLY1 = "rol\tr8,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Rolq.class);
    }
}
