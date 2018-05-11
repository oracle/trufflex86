package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.instruction.Shl.Shlq;
import org.graalvm.vm.x86.isa.test.CodeArrayReader;
import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class ShlTest {
    public static final byte[] MACHINECODE1 = {0x48, (byte) 0xc1, (byte) 0xe2, 0x20};
    public static final String ASSEMBLY1 = "shl\trdx,0x20";

    @Test
    public void test1() {
        CodeReader reader = new CodeArrayReader(MACHINECODE1, 0);
        AMD64Instruction insn = AMD64InstructionDecoder.decode(0, reader);
        assertNotNull(insn);
        assertTrue(insn instanceof Shlq);
        assertEquals(ASSEMBLY1, insn.toString());
        assertEquals(MACHINECODE1.length, reader.getPC());
    }

    @Test
    public void shl() throws Exception {
        String stdout = "0000:00:0000:0000\n" +
                        "0000:05:0000:0044\n" +
                        "1000:00:1000:0000\n" +
                        "1000:05:0000:0044\n" +
                        "ffff:05:ffe0:0081\n" +
                        "8000:08:0000:0044\n" +
                        "8001:0f:8000:0884\n" +
                        "8001:10:0000:0845\n" +
                        "ffff:0f:8000:0085\n" +
                        "ffff:10:0000:0845\n" +
                        "8000:01:0000:0845\n" +
                        "c000:01:8000:0085\n" +
                        "c000:02:0000:0845\n" +
                        "c000:02:0000:0845\n" +
                        "c000:04:0000:0044\n" +
                        "c000:00:c000:0000\n" +
                        "c0de:00:c0de:0000\n" +
                        "efde:08:de00:0085\n" +
                        "efde:04:fde0:0880\n" +
                        "0000:01:0000:0044\n" +
                        "1000:01:2000:0004\n" +
                        "0000:02:0000:0044\n" +
                        "1000:02:4000:0004\n" +
                        "c0de:10:0000:0044\n" +
                        "c0de:18:0000:0044\n" +
                        "c0de:20:c0de:0000\n" +
                        "c0de:30:0000:0044\n" +
                        "c0de:38:0000:0044\n" +
                        "c0de:40:c0de:0000\n" +
                        "dffffdea:1:bffffbd4:0085\n" +
                        "0ffffdea:1:1ffffbd4:0004\n" +
                        "0ffffde0:1:1ffffbc0:0004\n" +
                        "0000000000000163:20:0000016300000000:0004\n";
        TestRunner.run("shl.elf", new String[0], "", stdout, "", 0);
    }
}
