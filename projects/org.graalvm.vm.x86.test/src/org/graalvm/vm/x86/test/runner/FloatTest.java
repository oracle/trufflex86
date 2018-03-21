package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class FloatTest {
    @Test
    public void floatAdd() throws Exception {
        String stdout = "00000000:00000000:00000000\n" +
                        "3f800000:00000000:3f800000\n" +
                        "3f800000:3f800000:40000000\n" +
                        "41a80000:42280000:427c0000\n" +
                        "c1a80000:42280000:41a80000\n" +
                        "41a80000:c2280000:c1a80000\n" +
                        "322bcc77:3f800000:3f800000\n" +
                        "219392ef:3f800000:3f800000\n" +
                        "3f800000:322bcc77:3f800000\n" +
                        "3f800000:219392ef:3f800000\n" +
                        "4cbebc20:219392ef:4cbebc20\n" +
                        "322bcc77:5d5e0b6b:5d5e0b6b\n" +
                        "7fc00000:00000000:7fc00000\n" +
                        "7fc00000:3f800000:7fc00000\n" +
                        "7fc00000:bf800000:7fc00000\n" +
                        "7fc00000:7fc00000:7fc00000\n" +
                        "7f800000:00000000:7f800000\n" +
                        "7f800000:3f800000:7f800000\n" +
                        "7f800000:bf800000:7f800000\n" +
                        "7f800000:7f800000:7f800000\n" +
                        "7f800000:6c4ecb8f:7f800000\n" +
                        "7f800000:ec4ecb8f:7f800000\n" +
                        "6c4ecb8f:ec4ecb8f:00000000\n" +
                        "6c4ecb8f:6c4ecb8f:6ccecb8f\n";
        TestRunner.run("float-add.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void floatSub() throws Exception {
        String stdout = "00000000:00000000:00000000\n" +
                        "3f800000:00000000:3f800000\n" +
                        "3f800000:3f800000:00000000\n" +
                        "41a80000:42280000:c1a80000\n" +
                        "c1a80000:42280000:c27c0000\n" +
                        "41a80000:c2280000:427c0000\n" +
                        "322bcc77:3f800000:bf800000\n" +
                        "219392ef:3f800000:bf800000\n" +
                        "3f800000:322bcc77:3f800000\n" +
                        "3f800000:219392ef:3f800000\n" +
                        "4cbebc20:219392ef:4cbebc20\n" +
                        "322bcc77:5d5e0b6b:dd5e0b6b\n" +
                        "7fc00000:00000000:7fc00000\n" +
                        "7fc00000:3f800000:7fc00000\n" +
                        "7fc00000:bf800000:7fc00000\n" +
                        "7fc00000:7fc00000:7fc00000\n" +
                        "7f800000:00000000:7f800000\n" +
                        "7f800000:3f800000:7f800000\n" +
                        "7f800000:bf800000:7f800000\n" +
                        "7f800000:7f800000:7fc00000\n" +
                        "7f800000:6c4ecb8f:7f800000\n" +
                        "7f800000:ec4ecb8f:7f800000\n" +
                        "6c4ecb8f:ec4ecb8f:6ccecb8f\n" +
                        "6c4ecb8f:6c4ecb8f:00000000\n";
        TestRunner.run("float-sub.elf", new String[0], "", stdout, "", 0);
    }
}
