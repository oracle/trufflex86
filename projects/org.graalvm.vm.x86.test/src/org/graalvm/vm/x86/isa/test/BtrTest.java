package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Btr.Btrq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class BtrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x48, 0x0f, (byte) 0xba, (byte) 0xf0, 0x3f};
    private static final String ASSEMBLY1 = "btr\trax,0x3f";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Btrq.class);
    }
}
