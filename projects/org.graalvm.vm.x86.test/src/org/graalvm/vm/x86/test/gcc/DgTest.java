package org.graalvm.vm.x86.test.gcc;

import org.graalvm.vm.x86.test.runner.TestRunner;
import org.junit.Ignore;
import org.junit.Test;

public class DgTest {
    @Test
    public void pr26719() throws Exception {
        TestRunner.run("pr26719.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr36224() throws Exception {
        TestRunner.run("pr36224.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr46614() throws Exception {
        TestRunner.run("pr46614.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr48616() throws Exception {
        TestRunner.run("pr48616.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr50310_2() throws Exception {
        TestRunner.run("pr50310-2.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void pr57233() throws Exception {
        TestRunner.run("pr57233.elf", new String[0], "", "", "", 0);
    }

    @Ignore("Unaligned access using SSE")
    @Test
    public void pr87054() throws Exception {
        TestRunner.run("pr87054.elf", new String[0], "", "", "", 139);
    }

    @Test
    public void pr41551() throws Exception {
        TestRunner.run("pr41551.elf", new String[0], "", "", "", 0);
    }

    @Test
    public void nextafter_1() throws Exception {
        TestRunner.run("nextafter-1.elf", new String[0], "", "", "", 0);
    }
}
