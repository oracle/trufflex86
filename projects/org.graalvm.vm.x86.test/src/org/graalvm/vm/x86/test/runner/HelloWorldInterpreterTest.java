package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class HelloWorldInterpreterTest {
    @Test
    public void test1() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("hello.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test2() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("helloworld.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test3() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 1;

        TestRunner.run("hello-strlen.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test4() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("strlen.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test5() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("hello2.asm.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test6() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "fs(selector)=0\n" +
                        "fs=0\n" +
                        "fs(selector)=402048\n" +
                        "fs=402048\n" +
                        "tls=4847464544434241\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("tls.nostdlib.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test7() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "";
        String stderr = "";

        int code = 89;

        TestRunner.run("fib.asm.elf", args, stdin, stdout, stderr, code);
    }
}
