package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class SyscallTest {
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
}
