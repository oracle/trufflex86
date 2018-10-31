package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Ror.Rorb;
import org.graalvm.vm.x86.isa.instruction.Ror.Rorl;
import org.graalvm.vm.x86.isa.instruction.Ror.Rorq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class RorTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xd3, (byte) 0xce};
    private static final String ASSEMBLY1 = "ror\tesi,cl";

    private static final byte[] MACHINECODE2 = {0x48, (byte) 0xd1, (byte) 0xc9};
    private static final String ASSEMBLY2 = "ror\trcx,0x1";

    private static final byte[] MACHINECODE3 = {(byte) 0xd2, (byte) 0xc8};
    private static final String ASSEMBLY3 = "ror\tal,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Rorl.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Rorq.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE3, ASSEMBLY3, Rorb.class);
    }
}
