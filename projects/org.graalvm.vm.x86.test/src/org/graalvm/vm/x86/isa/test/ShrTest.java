package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.instruction.Shr.Shrb;
import org.graalvm.vm.x86.test.InstructionTest;
import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class ShrTest extends InstructionTest {
    private static final byte[] MACHINECODE1 = {0x41, (byte) 0xd2, (byte) 0xe9};
    private static final String ASSEMBLY1 = "shr\tr9b,cl";

    @Test
    public void test1() {
        check(MACHINECODE1, ASSEMBLY1, Shrb.class);
    }

    @Test
    public void shr() throws Exception {
        String stdout = "0000:00:0000:0000\n" +
                        "0000:05:0000:0044\n" +
                        "1000:00:1000:0000\n" +
                        "1000:05:0080:0000\n" +
                        "ffff:05:07ff:0805\n" +
                        "8000:08:0080:0800\n" +
                        "8001:0f:0001:0800\n" +
                        "8001:10:0000:0845\n" +
                        "ffff:0f:0001:0801\n" +
                        "ffff:10:0000:0845\n" +
                        "8000:01:4000:0804\n" +
                        "c000:01:6000:0804\n" +
                        "c000:02:3000:0804\n" +
                        "c000:02:3000:0804\n" +
                        "c000:04:0c00:0804\n" +
                        "c000:00:c000:0000\n" +
                        "c0de:00:c0de:0000\n" +
                        "efde:08:00ef:0801\n" +
                        "efde:04:0efd:0801\n" +
                        "0000:01:0000:0044\n" +
                        "1000:01:0800:0004\n" +
                        "0000:02:0000:0044\n" +
                        "1000:02:0400:0004\n" +
                        "c0de:10:0000:0845\n" +
                        "c0de:18:0000:0845\n" + // undefined: (i7: 0844)
                        "c0de:20:c0de:0000\n" +
                        "c0de:30:0000:0845\n" +
                        "c0de:38:0000:0845\n" + // undefined: (i7: 0844)
                        "c0de:40:c0de:0000\n" +
                        "0001:00:0001:0000\n" +
                        "0001:05:0000:0044\n" +
                        "0001:08:0000:0044\n" +
                        "0001:01:0000:0045\n" +
                        "0003:01:0001:0001\n" +
                        "0003:02:0000:0045\n" +
                        "0003:02:0000:0045\n" +
                        "0003:04:0000:0044\n" +
                        "0003:00:0003:0000\n" +
                        "0000:01:0000:0044\n" +
                        "0001:01:0000:0045\n" +
                        "0000:02:0000:0044\n" +
                        "0001:02:0000:0044\n" +
                        "dffffdea:1:6ffffef5:0804\n" +
                        "0ffffdea:1:07fffef5:0004\n" +
                        "0ffffde0:1:07fffef0:0004\n" +
                        "0ffffde1:1:07fffef0:0005\n" +
                        "0000000000000163:20:0000000000000000:0044\n" +
                        "1630000000000000:20:0000000016300000:0004\n";
        TestRunner.run("shr.elf", new String[0], "", stdout, "", 0);
    }
}
