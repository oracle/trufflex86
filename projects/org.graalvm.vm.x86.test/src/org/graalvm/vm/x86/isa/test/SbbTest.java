package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbb;
import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbl;
import org.graalvm.vm.x86.isa.instruction.Sbb.Sbbq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SbbTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x19, (byte) 0xc0};
    private static final String ASSEMBLY1 = "sbb\teax,eax";

    private static final byte[] MACHINECODE2 = {0x4c, 0x1b, 0x02};
    private static final String ASSEMBLY2 = "sbb\tr8,[rdx]";

    private static final byte[] MACHINECODE3 = {0x1c, 0x00};
    private static final String ASSEMBLY3 = "sbb\tal,0x0";

    private static final byte[] MACHINECODE4 = {0x41, (byte) 0x80, (byte) 0xd8, 0x00};
    private static final String ASSEMBLY4 = "sbb\tr8b,0x0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Sbbl.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Sbbq.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Sbbb.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Sbbb.class);
    }
}
