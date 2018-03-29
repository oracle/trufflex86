package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class SyscallTest {
    @Test
    public void id() throws Exception {
        TestRunner.run("syscall-id.elf", new String[0], "", "uid=1000, gid=1000\n", "", 0);
    }
}
