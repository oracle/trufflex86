package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class MiscTests {
    @Test
    public void iAmPure() throws Exception {
        TestRunner.run("i-am-pure.elf", new String[0], "", "0\n0\n", "", 0);
    }
}
