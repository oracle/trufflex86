package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Fldcw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class FldcwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xd9, 0x6c, 0x24, 0x06};
    private static final String ASSEMBLY1 = "fldcw\t[rsp+0x6]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Fldcw.class);
    }
}
