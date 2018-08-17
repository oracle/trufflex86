package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Tzcnt.Tzcntq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class TzcntTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x4d, 0x0f, (byte) 0xbc, 0x03};
    private static final String ASSEMBLY1 = "tzcnt\tr8,[r11]";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Tzcntq.class);
    }
}
