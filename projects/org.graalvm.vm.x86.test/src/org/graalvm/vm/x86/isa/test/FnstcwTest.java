package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Fnstcw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class FnstcwTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xd9, (byte) 0xbd, 0x5a, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    private static final String ASSEMBLY1 = "fnstcw\t[rbp-0xa6]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Fnstcw.class);
    }
}
