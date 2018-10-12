package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class IsaFeatures {
    @Test
    public void cpuidVendorBrand() throws Exception {
        String stdout = "Vendor:   'VMX86onGraal'\n" +
                        "Brand:    'VMX86 on Graal/Truffle'\n" +
                        "[info]:   0x00000611\n" +
                        "Family:   6\n" +
                        "Model:    1\n" +
                        "Stepping: 1\n" +
                        "Type:     0\n";
        TestRunner.run("cpuid.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void cpuidDetection() throws Exception {
        String stdout = "i586\n";
        TestRunner.run("cpuid-detect.asm.elf", new String[0], "", stdout, "", 0);
    }
}
