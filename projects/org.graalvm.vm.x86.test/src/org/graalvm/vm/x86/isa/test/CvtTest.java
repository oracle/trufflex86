package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Cvtdq2pd;
import org.graalvm.vm.x86.isa.instruction.Cvtdq2ps;
import org.graalvm.vm.x86.isa.instruction.Cvtpd2ps;
import org.graalvm.vm.x86.isa.instruction.Cvtps2dq;
import org.graalvm.vm.x86.isa.instruction.Cvtps2pd;
import org.graalvm.vm.x86.isa.instruction.Cvtsd2si.Cvtsd2siq;
import org.graalvm.vm.x86.isa.instruction.Cvtsd2ss;
import org.graalvm.vm.x86.isa.instruction.Cvtss2sd;
import org.graalvm.vm.x86.isa.instruction.Cvtss2si.Cvtss2siq;
import org.graalvm.vm.x86.isa.instruction.Cvttss2si;
import org.graalvm.vm.x86.test.InstructionTest;
import org.junit.Test;

public class CvtTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {(byte) 0xf3, 0x0f, (byte) 0xe6, (byte) 0xc1};
    private static final String ASSEMBLY1 = "cvtdq2pd\txmm0,xmm1";

    private static final byte[] MACHINECODE2 = {(byte) 0xf3, 0x0f, 0x2c, (byte) 0xc1};
    private static final String ASSEMBLY2 = "cvttss2si\teax,xmm1";

    private static final byte[] MACHINECODE3 = {(byte) 0xf3, 0x0f, 0x5a, (byte) 0xc0};
    private static final String ASSEMBLY3 = "cvtss2sd\txmm0,xmm0";

    private static final byte[] MACHINECODE4 = {(byte) 0xf2, 0x0f, 0x5a, (byte) 0xc9};
    private static final String ASSEMBLY4 = "cvtsd2ss\txmm1,xmm1";

    private static final byte[] MACHINECODE5 = {0x0f, 0x5a, (byte) 0xd0};
    private static final String ASSEMBLY5 = "cvtps2pd\txmm2,xmm0";

    private static final byte[] MACHINECODE6 = {0x66, 0x0f, 0x5a, (byte) 0xd2};
    private static final String ASSEMBLY6 = "cvtpd2ps\txmm2,xmm2";

    private static final byte[] MACHINECODE7 = {0x0f, 0x5b, (byte) 0xe4};
    private static final String ASSEMBLY7 = "cvtdq2ps\txmm4,xmm4";

    private static final byte[] MACHINECODE8 = {(byte) 0xf2, 0x48, 0x0f, 0x2d, (byte) 0xc1};
    private static final String ASSEMBLY8 = "cvtsd2si\trax,xmm1";

    private static final byte[] MACHINECODE9 = {(byte) 0xf3, 0x48, 0x0f, 0x2d, (byte) 0xf0};
    private static final String ASSEMBLY9 = "cvtss2si\trsi,xmm0";

    private static final byte[] MACHINECODE10 = {0x66, 0x0f, 0x5b, (byte) 0xc0};
    private static final String ASSEMBLY10 = "cvtps2dq\txmm0,xmm0";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Cvtdq2pd.class);
    }

    @Test
    public void test2() {
        check(MACHINECODE2, ASSEMBLY2, Cvttss2si.class);
    }

    @Test
    public void test3() {
        check(MACHINECODE3, ASSEMBLY3, Cvtss2sd.class);
    }

    @Test
    public void test4() {
        check(MACHINECODE4, ASSEMBLY4, Cvtsd2ss.class);
    }

    @Test
    public void test5() {
        check(MACHINECODE5, ASSEMBLY5, Cvtps2pd.class);
    }

    @Test
    public void test6() {
        check(MACHINECODE6, ASSEMBLY6, Cvtpd2ps.class);
    }

    @Test
    public void test7() {
        check(MACHINECODE7, ASSEMBLY7, Cvtdq2ps.class);
    }

    @Test
    public void test8() {
        check(MACHINECODE8, ASSEMBLY8, Cvtsd2siq.class);
    }

    @Test
    public void test9() {
        check(MACHINECODE9, ASSEMBLY9, Cvtss2siq.class);
    }

    @Test
    public void test10() {
        check(MACHINECODE10, ASSEMBLY10, Cvtps2dq.class);
    }
}
