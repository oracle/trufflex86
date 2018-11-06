package org.graalvm.vm.x86.test.gcc;

import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Test;

public class CCommonTest {
    @Test
    public void builtin_arith_overflow_2() throws Exception {
        TestRunner.run("builtin-arith-overflow-2.elf", new String[0], "", "", "", 0);
    }
}
