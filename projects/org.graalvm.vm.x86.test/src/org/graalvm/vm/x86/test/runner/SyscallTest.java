package org.graalvm.vm.x86.test.runner;

import java.util.Arrays;
import java.util.Base64;

import org.junit.Ignore;
import org.junit.Test;

public class SyscallTest {
    private static final String SHELLCODE_OUTPUT = "ylFerA8FuDwAAAAx/w8FSGVsb" +
                    "G8gd29ybGQhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwABA" +
                    "OgAQAAAAAAAAAAAAAAAAAAAAAAAAwACAAAQQAAAAAAAAAAAAAAAAAABA" +
                    "AAABADx/wAAAAAAAAAAAAAAAAAAAAAbAAAAAAACABgQQAAAAAAAAAAAA" +
                    "AAAAAAhAAAAAAACACEQQAAAAAAAAAAAAAAAAAAmAAAAAAACACoQQAAAA" +
                    "AAAAAAAAAAAAAAwAAAAEAACAAAQQAAAAAAAAAAAAAAAAAArAAAAEAACA" +
                    "AAgQAAAAAAAAAAAAAAAAAA3AAAAEAACAAAgQAAAAAAAAAAAAAAAAAA+A" +
                    "AAAEAACAAAgQAAAAAAAAAAAAAAAAAAAc3JjL3NoZWxsY29kZS05Ynl0Z" +
                    "S5hc20ubwBzaGVsbABleGl0AHRleHQAX19ic3Nfc3RhcnQAX2VkYXRhA" +
                    "F9lbmQAAC5zeW10YWIALnN0cnRhYgAuc2hzdHJ0YWIALm5vdGUuZ251L" +
                    "nByb3BlcnR5AC50ZXh0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGwAAA" +
                    "AcAAAACAAAAAAAAAOgAQAAAAAAA6AAAAAAAAAAgAAAAAAAAAAAAAAAAA" +
                    "AAACAAAAAAAAAAAAAAAAAAAAC4AAAABAAAABgAAAAAAAAAAEEAAAAAAA" +
                    "AAQAAAAAAAANgAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAABA" +
                    "AAAAgAAAAAAAAAAAAAAAAAAAAAAAAA4EAAAAAAAAAgBAAAAAAAABAAAA" +
                    "AcAAAAIAAAAAAAAABgAAAAAAAAACQAAAAMAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAQBEAAAAAAABDAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAA" +
                    "BEAAAADAAAAAAAAAAAAAAAAAAAAAAAAAIMRAAAAAAAANAAAAAAAAAAAA" +
                    "AAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAA=";

    @Test
    public void id() throws Exception {
        TestRunner.run("syscall-id.elf", new String[0], "", "uid=1000, gid=1000\n", "", 0);
    }

    @Test
    public void sc0() throws Exception {
        TestRunner.run("sc0.asm.elf", new String[0], "", "", "", 14);
    }

    @Test
    public void syscallRegs() throws Exception {
        TestRunner.run("syscall-regs.elf", new String[0], "", "rcx = 000000000040159a\nr11 = 0000000000000206\n", "", 0);
    }

    @Ignore
    @Test
    public void shellcode9Bytes() throws Exception {
        TestRunner.runBinary("shellcode-9byte.asm.elf", new String[0], "", SHELLCODE_OUTPUT, "", 0);
    }

    @Test
    public void testWriteNoFault() throws Exception {
        byte[] buf = "Hello world!\n".getBytes();
        String stdout = Base64.getEncoder().encodeToString(Arrays.copyOf(buf, 4096));
        TestRunner.runBinary("write-long.elf", new String[0], "", stdout, "", 0);
    }
}
