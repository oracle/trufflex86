package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Endbr32;
import org.graalvm.vm.x86.isa.instruction.Endbr64;
import org.graalvm.vm.x86.isa.instruction.Rdssp.Rdsspq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CetTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, 0x1e, (byte) 0xfa};
    private static final String ASSEMBLY1 = "endbr64";

    private static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, 0x1e, (byte) 0xfb};
    private static final String ASSEMBLY2 = "endbr32";

    private static final byte[] MACHINECODE3 = {(byte) 0xf3, 0x48, 0x0f, 0x1e, (byte) 0xc8};
    private static final String ASSEMBLY3 = "rdsspq\trax";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Endbr64.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Endbr32.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Rdsspq.class);
    }
}
