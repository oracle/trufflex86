package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psub.Psubd;
import org.graalvm.vm.x86.isa.instruction.Psub.Psubq;
import org.graalvm.vm.x86.isa.instruction.Psub.Psubw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsubTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xfa, (byte) 0xe7};
    private static final String ASSEMBLY1 = "psubd\txmm4,xmm7";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xfb, (byte) 0xf3};
    private static final String ASSEMBLY2 = "psubq\txmm6,xmm3";

    private static final byte[] MACHINECODE3 = {0x66, 0x0f, (byte) 0xf9, (byte) 0xf2};
    private static final String ASSEMBLY3 = "psubw\txmm6,xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psubd.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psubq.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Psubw.class);
    }
}
