package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Dec.Decb;
import org.graalvm.vm.x86.isa.instruction.Dec.Decl;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class DecTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, (byte) 0xfe, (byte) 0xcd};
    private static final String ASSEMBLY1 = "dec\tr13b";

    private static final byte[] MACHINECODE2 = {(byte) 0xff, (byte) 0xc9};
    private static final String ASSEMBLY2 = "dec\tecx";

    private static final byte[] MACHINECODE3 = {(byte) 0xff, (byte) 0x8b, (byte) 0xd8, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY3 = "dec\t[rbx+0xd8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Decb.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Decl.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Decl.class);
    }
}
