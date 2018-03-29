package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class MiscTests {
    @Test
    public void iAmPure() throws Exception {
        TestRunner.run("i-am-pure.elf", new String[0], "", "0\n0\n", "", 0);
    }

    @Test
    public void dlIteratePhdr() throws Exception {
        String stdout = "Name: \"\" (6 segments)\n" +
                        "     0: [      0x400000; memsz:  94496] flags: 0x5; PT_LOAD\n" +
                        "     1: [      0x6945c0; memsz:   61f0] flags: 0x6; PT_LOAD\n" +
                        "     2: [      0x400190; memsz:     44] flags: 0x4; PT_NOTE\n" +
                        "     3: [      0x6945c0; memsz:     60] flags: 0x4; PT_TLS\n" +
                        "     4: [         (nil); memsz:      0] flags: 0x6; PT_GNU_STACK\n" +
                        "     5: [      0x6945c0; memsz:   2a40] flags: 0x4; PT_GNU_RELRO\n";
        TestRunner.run("dl_iterate_phdr.elf", new String[0], "", stdout, "", 0);
    }
}
