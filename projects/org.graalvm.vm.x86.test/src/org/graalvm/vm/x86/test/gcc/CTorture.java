package org.graalvm.vm.x86.test.gcc;

import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class CTorture {
    @Test
    public void pr50310() throws Exception {
        TestRunner.run("pr50310.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr28982a() throws Exception {
        TestRunner.run("pr28982a.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr28982b() throws Exception {
        TestRunner.run("pr28982b.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void ssad_run() throws Exception {
        TestRunner.run("ssad-run.elf", new String[0], "", "", "", 0);
    }
}
