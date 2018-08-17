package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Xor.Xorb;
import org.graalvm.vm.x86.isa.instruction.Xor.Xorl;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class XorTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0x83, (byte) 0xf0, 0x01};
    private static final String ASSEMBLY1 = "xor\teax,0x1";

    private static final byte[] MACHINECODE2 = {0x34, (byte) 0xff};
    private static final String ASSEMBLY2 = "xor\tal,-0x1";

    private static final byte[] MACHINECODE3 = {(byte) 0x80, (byte) 0xf2, (byte) 0xff};
    private static final String ASSEMBLY3 = "xor\tdl,-0x1";

    private static final byte[] MACHINECODE4 = {0x41, (byte) 0x81, (byte) 0xf6, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x3f};
    private static final String ASSEMBLY4 = "xor\tr14d,0x3fffffff";

    private static final byte[] MACHINECODE5 = {(byte) 0x81, (byte) 0xf6, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x3f};
    private static final String ASSEMBLY5 = "xor\tesi,0x3fffffff";

    private static final byte[] MACHINECODE6 = {0x32, 0x46, 0x1a};
    private static final String ASSEMBLY6 = "xor\tal,[rsi+0x1a]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Xorl.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Xorb.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Xorb.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Xorl.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Xorl.class);
    }

    @Test
    public void test6() {
        check(MACHINECODE6, ASSEMBLY6, Xorb.class);
    }
}
