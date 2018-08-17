package org.graalvm.vm.x86.test;

import org.graalvm.vm.x86.isa.instruction.Call.CallAbsolute;
import org.graalvm.vm.x86.isa.instruction.Lea;
import org.graalvm.vm.x86.isa.instruction.Mov.Movq;
import org.graalvm.vm.x86.isa.instruction.Movsxd;
import org.junit.Test;

public class ModRMTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x64, 0x48, (byte) 0x89, 0x04, 0x25, 0x28, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY1 = "mov\tfs:[0x28],rax";

    private static final byte[] MACHINECODE2 = {(byte) 0xff, 0x54, (byte) 0xdd, 0x00};
    private static final String ASSEMBLY2 = "call\t[rbp+rbx*8]";

    private static final byte[] MACHINECODE3 = {0x4a, 0x63, 0x04, (byte) 0xa2};
    private static final String ASSEMBLY3 = "movsxd\trax,[rdx+r12*4]";

    private static final byte[] MACHINECODE4 = {0x4a, (byte) 0x8d, 0x14, (byte) 0xa5, 0x00, 0x00, 0x00, 0x00};
    private static final String ASSEMBLY4 = "lea\trdx,[r12*4]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Movq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, CallAbsolute.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Movsxd.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Lea.class);
    }
}
