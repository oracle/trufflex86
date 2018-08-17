package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psll.Pslld;
import org.graalvm.vm.x86.isa.instruction.Psll.Psllq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsllTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x72, (byte) 0xf1, 0x03};
    private static final String ASSEMBLY1 = "pslld\txmm1,0x3";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x73, (byte) 0xf7, 0x20};
    private static final String ASSEMBLY2 = "psllq\txmm7,0x20";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pslld.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psllq.class);
    }
}
