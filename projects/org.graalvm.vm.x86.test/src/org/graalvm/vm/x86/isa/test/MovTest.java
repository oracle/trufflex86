package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Mov;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class MovTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, (byte) 0xb9, (byte) 0xcd, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc};
    private static final String ASSEMBLY1 = "movabs\trcx,0xcccccccccccccccd";

    private static final byte[] MACHINECODE2 = {0x48, (byte) 0x8b, 0x04, 0x24};
    private static final String ASSEMBLY2 = "mov\trax,[rsp]";

    private static final byte[] MACHINECODE3 = {(byte) 0x8a, 0x06};
    private static final String ASSEMBLY3 = "mov\tal,[rsi]";

    private static final byte[] MACHINECODE4 = {0x49, (byte) 0xb9, (byte) 0xd0, 0x03, 0x00, (byte) 0x80, 0x03, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY4 = "movabs\tr9,0x3800003d0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Mov.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Mov.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Mov.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Movq.class);
    }
}
