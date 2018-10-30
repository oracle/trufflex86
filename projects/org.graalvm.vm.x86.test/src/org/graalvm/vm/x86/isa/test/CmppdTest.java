package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cmppd;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CmppdTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xcf, 0x02};
    private static final String ASSEMBLY1 = "cmplepd\txmm1,xmm7";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xf2, 0x05};
    private static final String ASSEMBLY2 = "cmpnltpd\txmm6,xmm2";

    private static final byte[] MACHINECODE3 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xf2, 0x03};
    private static final String ASSEMBLY3 = "cmpunordpd\txmm6,xmm2";

    private static final byte[] MACHINECODE4 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xea, 0x00};
    private static final String ASSEMBLY4 = "cmpeqpd\txmm5,xmm2";

    private static final byte[] MACHINECODE5 = {0x66, 0x0f, (byte) 0xc2, (byte) 0xea, 0x07};
    private static final String ASSEMBLY5 = "cmpordpd\txmm5,xmm2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cmppd.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Cmppd.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Cmppd.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Cmppd.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Cmppd.class);
    }
}
