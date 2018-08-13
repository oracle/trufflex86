package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class MiscTests {
    @Test
    public void iAmPure() throws Exception {
        TestRunner.run("i-am-pure.elf", new String[0], "", "0\n0\n", "", 0);
    }

    @Test
    public void dlIteratePhdr() throws Exception {
        String stdout = "Name: \"\" (8 segments)\n" +
                        "     0: [      0x400000; memsz:    470] flags: 0x4; PT_LOAD\n" +
                        "     1: [      0x401000; memsz:  799cd] flags: 0x5; PT_LOAD\n" +
                        "     2: [      0x47b000; memsz:  227ef] flags: 0x4; PT_LOAD\n" +
                        "     3: [      0x49f300; memsz:   6530] flags: 0x6; PT_LOAD\n" +
                        "     4: [      0x400200; memsz:     44] flags: 0x4; PT_NOTE\n" +
                        "     5: [      0x49f300; memsz:     60] flags: 0x4; PT_TLS\n" +
                        "     6: [         (nil); memsz:      0] flags: 0x6; PT_GNU_STACK\n" +
                        "     7: [      0x49f300; memsz:   2d00] flags: 0x4; PT_GNU_RELRO\n";
        TestRunner.run("dl_iterate_phdr.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void ftell() throws Exception {
        String stdout = "Running test case \"(fh = tmpfile()) != NULL\"\n" +
                        "Running test case \"setvbuf(fh, buffer, _IOLBF, 4) == 0\"\n" +
                        "Running test case \"fseek(fh, 0L, SEEK_SET) == 0\"\n" +
                        "Running test case \"ungetc('x', fh) == 'x'\"\n" +
                        "Running test case \"ftell(fh) == -1l\"\n" +
                        "Running test case \"fseek(fh, 0L, SEEK_SET) == 0\"\n" +
                        "Running test case \"fputc('1', fh) == '1'\"\n" +
                        "EXIT()\n";
        TestRunner.run("ftell.elf", new String[0], "", stdout, "", 0);
    }
}
