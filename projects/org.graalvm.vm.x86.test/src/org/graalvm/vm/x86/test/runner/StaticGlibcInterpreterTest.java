package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class StaticGlibcInterpreterTest {
    @Test
    public void test1() throws Exception {
        TestRunner.run("hello.elf", new String[0], "", "Hello world!\n", "", 42);
    }

    @Test
    public void test2() throws Exception {
        TestRunner.run("memcpy.elf", new String[0], "", "Hello world!\n", "", 0);
    }

    @Test
    public void test3() throws Exception {
        TestRunner.run("printf.elf", new String[0], "", "Answer: 42\n" +
                        "Answer: 432\n" +
                        "Answer: 24\n", "", 21);
    }

    @Test
    public void test4() throws Exception {
        TestRunner.run("endianess.elf", new String[0], "", "Bytes: F0 DE BC 9A 78 56 34 12\n", "", 0);
    }

    @Test
    public void longjmp() throws Exception {
        String stdout = "Start\n" +
                        "zero\n" +
                        "non-zero\n" +
                        "another setjmp\n" +
                        "call\n" +
                        "calling longjmp\n" +
                        "exception!\n" +
                        "yet another setjmp\n";
        TestRunner.run("longjmp.elf", new String[0], "", stdout, "", 0);
    }

    @Test
    public void atexit() throws Exception {
        String stdout = "main\n" +
                        "atexit hook 2\n" +
                        "atexit hook 1\n";
        TestRunner.run("atexit.elf", new String[0], "", stdout, "", 0);
    }
}
