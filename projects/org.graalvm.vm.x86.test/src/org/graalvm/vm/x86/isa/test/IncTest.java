package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Inc.Incb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class IncTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xfe, (byte) 0xc0};
    private static final String ASSEMBLY1 = "inc\tal";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Incb.class);
    }
}
