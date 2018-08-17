package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Adc.Adcb;
import org.graalvm.vm.x86.isa.instruction.Adc.Adcl;
import org.graalvm.vm.x86.isa.instruction.Adc.Adcq;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class AdcTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0x83, (byte) 0xd0, (byte) 0xff};
    private static final String ASSEMBLY1 = "adc\teax,-0x1";

    private static final byte[] MACHINECODE2 = {0x41, 0x11, (byte) 0xf8};
    private static final String ASSEMBLY2 = "adc\tr8d,edi";

    private static final byte[] MACHINECODE3 = {0x4c, 0x13, 0x02};
    private static final String ASSEMBLY3 = "adc\tr8,[rdx]";

    private static final byte[] MACHINECODE4 = {0x14, (byte) 0xff};
    private static final String ASSEMBLY4 = "adc\tal,-0x1";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Adcl.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Adcl.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Adcq.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Adcb.class);
    }
}
