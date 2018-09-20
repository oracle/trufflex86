package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Pshufd;
import org.graalvm.vm.x86.isa.instruction.Pshufhw;
import org.graalvm.vm.x86.isa.instruction.Pshuflw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PshufTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf2, 0x0f, 0x70, (byte) 0xac, (byte) 0x8c, 0x10, 0x04, 0x00, 0x00, (byte) 0xe8};
    private static final String ASSEMBLY1 = "pshuflw\txmm5,[rsp+rcx*4+0x410],0xe8";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x70, (byte) 0xf6, (byte) 0xe8};
    private static final String ASSEMBLY2 = "pshufd\txmm6,xmm6,0xe8";

    private static final byte[] MACHINECODE3 = {(byte) 0xf3, 0x0f, 0x70, (byte) 0xed, (byte) 0xe8};
    private static final String ASSEMBLY3 = "pshufhw\txmm5,xmm5,0xe8";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Pshuflw.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Pshufd.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Pshufhw.class);
    }
}
