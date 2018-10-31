package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Rol.Rolb;
import org.graalvm.vm.x86.isa.instruction.Rol.Rolq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class RolTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x49, (byte) 0xd3, (byte) 0xc0};
    private static final String ASSEMBLY1 = "rol\tr8,cl";

    private static final byte[] MACHINECODE2 = {(byte) 0xd2, (byte) 0xc0};
    private static final String ASSEMBLY2 = "rol\tal,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Rolq.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE2, ASSEMBLY2, Rolb.class);
    }
}
