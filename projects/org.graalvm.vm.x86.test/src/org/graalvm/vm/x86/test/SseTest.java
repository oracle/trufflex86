package org.graalvm.vm.x86.test;

import org.graalvm.vm.x86.isa.instruction.Pand;
import org.graalvm.vm.x86.isa.instruction.Pcmpgt.Pcmpgt128b;
import org.graalvm.vm.x86.isa.instruction.Pmaxub;
import org.junit.Test;

public class SseTest extends InstructionTest {
    private static final byte[] MACHINECODE_PMAXUB = {0x66, 0x0f, (byte) 0xde, (byte) 0xd8};
    private static final String ASSEMBLY_PMAXUB = "pmaxub\txmm3,xmm0";

    private static final byte[] MACHINECODE_PCMPGTB = {0x66, 0x44, 0x0f, 0x64, (byte) 0xc5};
    private static final String ASSEMBLY_PCMPGTB = "pcmpgtb\txmm8,xmm5";

    private static final byte[] MACHINECODE_PAND = {0x66, 0x45, 0x0f, (byte) 0xdb, (byte) 0xc1};
    private static final String ASSEMBLY_PAND = "pand\txmm8,xmm9";

    @Test
    public void testPmaxub() {
        check(MACHINECODE_PMAXUB, ASSEMBLY_PMAXUB, Pmaxub.class);
    }

    @Test
    public void testPcmpgtb() {
        check(MACHINECODE_PCMPGTB, ASSEMBLY_PCMPGTB, Pcmpgt128b.class);
    }

    @Test
    public void testPand() {
        check(MACHINECODE_PAND, ASSEMBLY_PAND, Pand.class);
    }
}
