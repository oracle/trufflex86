package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Bswap.Bswapl;
import org.graalvm.vm.x86.isa.instruction.Bswap.Bswapq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class BswapTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xc8};
    private static final String ASSEMBLY1 = "bswap\teax";

    private static final byte[] MACHINECODE2 = {0x48, 0x0f, (byte) 0xca};
    private static final String ASSEMBLY2 = "bswap\trdx";

    private static final byte[] MACHINECODE3 = {0x41, 0x0f, (byte) 0xc9};
    private static final String ASSEMBLY3 = "bswap\tr9d";

    private static final byte[] MACHINECODE4 = {0x41, 0x0f, (byte) 0xcf};
    private static final String ASSEMBLY4 = "bswap\tr15d";

    private static final byte[] MACHINECODE5 = {0x49, 0x0f, (byte) 0xcf};
    private static final String ASSEMBLY5 = "bswap\tr15";

    private static final byte[] MACHINECODE6 = {0x0f, (byte) 0xc9};
    private static final String ASSEMBLY6 = "bswap\tecx";

    private static final byte[] MACHINECODE7 = {0x48, 0x0f, (byte) 0xc9};
    private static final String ASSEMBLY7 = "bswap\trcx";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Bswapl.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Bswapq.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Bswapl.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Bswapl.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Bswapq.class);
    }

    @Test
    public void test6() {
        check(MACHINECODE6, ASSEMBLY6, Bswapl.class);
    }

    @Test
    public void test7() {
        check(MACHINECODE7, ASSEMBLY7, Bswapq.class);
    }
}
