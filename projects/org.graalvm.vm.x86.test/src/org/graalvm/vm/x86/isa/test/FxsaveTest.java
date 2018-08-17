package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Fxrstor;
import org.graalvm.vm.x86.isa.instruction.Fxsave;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class FxsaveTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x0f, (byte) 0xae, (byte) 0x44, (byte) 0x24, (byte) 0x40};
    private static final String ASSEMBLY1 = "fxsave\t[rsp+0x40]";

    private static final byte[] MACHINECODE2 = {0x0f, (byte) 0xae, 0x4c, 0x24, 0x40};
    private static final String ASSEMBLY2 = "fxrstor\t[rsp+0x40]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Fxsave.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Fxrstor.class);
    }
}
