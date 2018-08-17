package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Punpckl.Punpckldq;
import org.graalvm.vm.x86.isa.instruction.Punpckl.Punpcklqdq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PunpcklTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x62, (byte) 0xc1};
    private static final String ASSEMBLY1 = "punpckldq\txmm0,xmm9";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x6c, (byte) 0xc0};
    private static final String ASSEMBLY2 = "punpcklqdq\txmm0,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Punpckldq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Punpcklqdq.class);
    }
}
