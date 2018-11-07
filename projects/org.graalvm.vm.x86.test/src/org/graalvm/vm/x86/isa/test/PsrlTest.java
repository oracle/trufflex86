package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Psrl.Psrld;
import org.graalvm.vm.x86.isa.instruction.Psrl.Psrlq;
import org.graalvm.vm.x86.isa.instruction.Psrl.Psrlw;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class PsrlTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x66, 0x41, 0x0f, 0x73, (byte) 0xd1, 0x20};
    private static final String ASSEMBLY1 = "psrlq\txmm9,0x20";

    private static final byte[] MACHINECODE2 = {0x66, 0x0f, 0x72, (byte) 0xd0, 0x1f};
    private static final String ASSEMBLY2 = "psrld\txmm0,0x1f";

    private static final byte[] MACHINECODE3 = {0x66, 0x0f, (byte) 0xd2, (byte) 0xc3};
    private static final String ASSEMBLY3 = "psrld\txmm0,xmm3";

    private static final byte[] MACHINECODE4 = {0x66, 0x0f, 0x71, (byte) 0xd4, 0x08};
    private static final String ASSEMBLY4 = "psrlw\txmm4,0x8";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Psrlq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Psrld.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Psrld.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Psrlw.class);
    }
}
