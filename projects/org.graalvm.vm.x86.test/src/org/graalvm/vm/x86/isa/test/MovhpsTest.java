package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Movhps.MovhpsToReg;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovhpsTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, 0x16, 0x44, 0x24, 0x28};
    private static final String ASSEMBLY1 = "movhps\txmm0,[rsp+0x28]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, MovhpsToReg.class);
    }
}
