package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.x86.isa.CpuidBits;
import org.junit.Test;

public class CpuidTest {
    @Test
    public void test() {
        int ref = 0x000306c3;
        int act = CpuidBits.getProcessorInfo(0, 6, 60, 3);
        assertEquals(ref, act);
    }
}
