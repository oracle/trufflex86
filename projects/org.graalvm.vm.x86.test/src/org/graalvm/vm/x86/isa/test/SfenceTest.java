package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Sfence;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SfenceTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0xf8};
    private static final String ASSEMBLY1 = "sfence";

    private static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, (byte) 0xf9};
    private static final String ASSEMBLY2 = "sfence";

    private static final byte[] MACHINECODE3 = {0x0f, (byte) 0xae, (byte) 0xfa};
    private static final String ASSEMBLY3 = "sfence";

    private static final byte[] MACHINECODE4 = {0x0f, (byte) 0xae, (byte) 0xfb};
    private static final String ASSEMBLY4 = "sfence";

    private static final byte[] MACHINECODE5 = {0x0f, (byte) 0xae, (byte) 0xfc};
    private static final String ASSEMBLY5 = "sfence";

    private static final byte[] MACHINECODE6 = {0x0f, (byte) 0xae, (byte) 0xfd};
    private static final String ASSEMBLY6 = "sfence";

    private static final byte[] MACHINECODE7 = {0x0f, (byte) 0xae, (byte) 0xfe};
    private static final String ASSEMBLY7 = "sfence";

    private static final byte[] MACHINECODE8 = {0x0f, (byte) 0xae, (byte) 0xff};
    private static final String ASSEMBLY8 = "sfence";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Sfence.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Sfence.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Sfence.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Sfence.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Sfence.class);
    }

    @Test
    public void test6() {
        check(MACHINECODE6, ASSEMBLY6, Sfence.class);
    }

    @Test
    public void test7() {
        check(MACHINECODE7, ASSEMBLY7, Sfence.class);
    }

    @Test
    public void test8() {
        check(MACHINECODE8, ASSEMBLY8, Sfence.class);
    }
}
