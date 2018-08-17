package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Or.Orb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class OrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0a, 0x5c, 0x24, 0x38};
    private static final String ASSEMBLY1 = "or\tbl,[rsp+0x38]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Orb.class);
    }
}
