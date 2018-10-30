package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Add.Addb;
import org.graalvm.vm.x86.isa.instruction.Add.Addq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AddTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x49, 0x01, (byte) 0xc4};
    private static final String ASSEMBLY1 = "add\tr12,rax";

    private static final byte[] MACHINECODE2 = {(byte) 0x80, 0x47, 0x18, 0x01};
    private static final String ASSEMBLY2 = "add\t[rdi+0x18],0x1";

    private static final byte[] MACHINECODE3 = {0x04, 0x01};
    private static final String ASSEMBLY3 = "add\tal,0x1";

    private static final byte[] MACHINECODE4 = {0x02, 0x4f, 0x08};
    private static final String ASSEMBLY4 = "add\tcl,[rdi+0x8]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Addq.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Addb.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Addb.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Addb.class);
    }
}
