package org.graalvm.vm.x86.isa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Shrd;
import org.graalvm.vm.x86.test.CodeArrayReader;
import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class ShrdTest {
    public static final byte[] MACHINECODE1 = {0x4c, 0x0f, (byte) 0xad, (byte) 0xc0};
    public static final String ASSEMBLY1 = "shrd\trax,r8,cl";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Shrd);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void shrd() throws Exception {
        String stdout = "0000:0000:00:0000:0000\n" +
                        "0000:0000:05:0000:0044\n" +
                        "0000:1000:00:0000:0000\n" +
                        "0000:1000:05:0000:0044\n" +
                        "0000:ffff:05:f800:0884\n" +
                        "0000:8000:08:0000:0044\n" +
                        "0000:8001:0f:0002:0000\n" +
                        "0000:8001:10:8001:0880\n" +
                        "0000:ffff:0f:fffe:0880\n" +
                        "0000:ffff:10:ffff:0884\n" +
                        "0001:8000:01:0000:0045\n" +
                        "0001:c000:01:0000:0045\n" +
                        "0001:c000:02:0000:0044\n" +
                        "1001:c000:02:0400:0004\n" +
                        "1001:c000:04:0100:0004\n" +
                        "1001:c000:00:1001:0000\n" +
                        "beef:c0de:00:beef:0000\n" +
                        "babe:efde:08:deba:0081\n" +
                        "babe:efde:04:ebab:0081\n" +
                        "4000:0000:01:2000:0004\n" +
                        "8000:1000:01:4000:0804\n" +
                        "4000:0000:02:1000:0004\n" +
                        "8000:1000:02:2000:0804\n" +
                        "beef:c0de:10:c0de:0085\n" + // undefined
                        "beef:c0de:18:0000:0845\n" + // undefined (i7: efc0:0885)
                        "beef:c0de:20:beef:0000\n" + // undefined
                        "beef:c0de:30:c0de:0085\n" + // undefined
                        "beef:c0de:38:0000:0845\n" + // undefined (i7: efc0:0885)
                        "beef:c0de:40:beef:0000\n" + // undefined
                        "0000:0001:01:8000:0884\n" +
                        "0001:0001:01:8000:0885\n" +
                        "0000:0001:08:0100:0004\n" +
                        "0008:0001:08:0100:0004\n" +
                        "1008:1001:04:1100:0005\n" +
                        "1008:1111:06:4440:0000\n" +
                        "4488:1111:03:2891:0000\n" +
                        "8000:0002:01:4000:0804\n" +
                        "4000:0001:01:a000:0884\n" +
                        "4000:0012:02:9000:0884\n" +
                        "----\n" +
                        "0000000000000000:000000000000000a:04:a000000000000000:0884\n";
        TestRunner.run("shrd.elf", new String[0], "", stdout, "", 0);
    }
}
