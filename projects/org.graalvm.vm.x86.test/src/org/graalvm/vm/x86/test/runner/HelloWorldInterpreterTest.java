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

        TestRunner.run("hello.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test2() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("helloworld.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test3() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 1;

        TestRunner.run("hello-strlen.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test4() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("strlen.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test5() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "Hello world!\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("hello2.elf", args, stdin, stdout, stderr, code);
    }

    @Test
    public void test6() throws Exception {
        String[] args = new String[0];

        String stdin = "";
        String stdout = "fs(selector)=0\n" +
                        "fs=0\n" +
                        "fs(selector)=400f98\n" +
                        "fs=400f98\n" +
                        "tls=4847464544434241\n";
        String stderr = "";

        int code = 0;

        TestRunner.run("tls.elf", args, stdin, stdout, stderr, code);
    }
}
