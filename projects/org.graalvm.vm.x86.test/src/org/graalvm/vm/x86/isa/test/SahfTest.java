package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Lahf;
import org.graalvm.vm.x86.isa.instruction.Sahf;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class SahfTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0x9e};
    private static final String ASSEMBLY1 = "sahf";

    private static final byte[] MACHINECODE2 = {(byte) 0x9f};
    private static final String ASSEMBLY2 = "lahf";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Sahf.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Lahf.class);
    }
}
