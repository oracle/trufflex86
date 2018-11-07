package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psll.Pslld;
import org.graalvm.vm.x86.isa.instruction.Psll.Psllq;
import org.graalvm.vm.x86.isa.instruction.Psll.Psllw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsllTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x0f, 0x72, (byte) 0xf1, 0x03};
    private static final String ASSEMBLY1 = "pslld\txmm1,0x3";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x73, (byte) 0xf7, 0x20};
    private static final String ASSEMBLY2 = "psllq\txmm7,0x20";

    private static final byte[] MACHINECODE3 = {0x66, 0x0f, (byte) 0xf2, (byte) 0xd1};
    private static final String ASSEMBLY3 = "pslld\txmm2,xmm1";

    private static final byte[] MACHINECODE4 = {0x66, 0x0f, 0x71, (byte) 0xf5, 0x02};
    private static final String ASSEMBLY4 = "psllw\txmm5,0x2";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pslld.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psllq.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Pslld.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Psllw.class);
    }
}
