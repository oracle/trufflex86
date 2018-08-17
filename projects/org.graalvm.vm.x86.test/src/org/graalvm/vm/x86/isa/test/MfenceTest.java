package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Mfence;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MfenceTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0xf0};
    private static final String ASSEMBLY1 = "mfence";

    private static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, (byte) 0xf1};
    private static final String ASSEMBLY2 = "mfence";

    private static final byte[] MACHINECODE3 = {0x0f, (byte) 0xae, (byte) 0xf2};
    private static final String ASSEMBLY3 = "mfence";

    private static final byte[] MACHINECODE4 = {0x0f, (byte) 0xae, (byte) 0xf3};
    private static final String ASSEMBLY4 = "mfence";

    private static final byte[] MACHINECODE5 = {0x0f, (byte) 0xae, (byte) 0xf4};
    private static final String ASSEMBLY5 = "mfence";

    private static final byte[] MACHINECODE6 = {0x0f, (byte) 0xae, (byte) 0xf5};
    private static final String ASSEMBLY6 = "mfence";

    private static final byte[] MACHINECODE7 = {0x0f, (byte) 0xae, (byte) 0xf6};
    private static final String ASSEMBLY7 = "mfence";

    private static final byte[] MACHINECODE8 = {0x0f, (byte) 0xae, (byte) 0xf7};
    private static final String ASSEMBLY8 = "mfence";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Mfence.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Mfence.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Mfence.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Mfence.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Mfence.class);
    }

    @Test
    public void test6() {
        check(MACHINECODE6, ASSEMBLY6, Mfence.class);
    }

    @Test
    public void test7() {
        check(MACHINECODE7, ASSEMBLY7, Mfence.class);
    }

    @Test
    public void test8() {
        check(MACHINECODE8, ASSEMBLY8, Mfence.class);
    }
}
