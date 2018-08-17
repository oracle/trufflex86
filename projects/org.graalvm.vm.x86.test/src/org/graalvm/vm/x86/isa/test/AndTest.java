package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.And.Andb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AndTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x22, 0x44, 0x24, 0x0d};
    private static final String ASSEMBLY1 = "and\tal,[rsp+0xd]";

    private static final byte[] MACHINECODE2 = {0x20, (byte) 0xc8};
    private static final String ASSEMBLY2 = "and\tal,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Andb.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Andb.class);
    }
}
