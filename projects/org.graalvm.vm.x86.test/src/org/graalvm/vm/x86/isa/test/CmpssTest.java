package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cmpss.Cmpltss;
import org.graalvm.vm.x86.isa.instruction.Cmpss.Cmpnltss;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CmpssTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x44, 0x0f, (byte) 0xc2, (byte) 0xf9, 0x01};
    private static final String ASSEMBLY1 = "cmpltss\txmm15,xmm1";

    private static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, (byte) 0xc2, (byte) 0xf1, 0x05};
    private static final String ASSEMBLY2 = "cmpnltss\txmm6,xmm1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cmpltss.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Cmpnltss.class);
    }
}
