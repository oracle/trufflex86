package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class ArgsTest {
    private static final String FILENAME = "args.nostdlib.elf";

    private static String get(String[] args) {
        StringBuilder buf = new StringBuilder("Arguments: ");
        buf.append(args.length + 1);
        buf.append("\nargs[0] = '");
        buf.append(TestRunner.getPath(FILENAME));
        buf.append("'\n");
        for (int i = 0; i < args.length; i++) {
            buf.append("args[").append(i + 1).append("] = '").append(args[i]).append("'\n");
        }
        return buf.toString();
    }

    @Test
    public void test1() throws Exception {
        String[] args = new String[0];
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test2() throws Exception {
        String[] args = {"Hello"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test3() throws Exception {
        String[] args = {"Hello", "World"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test4() throws Exception {
        String[] args = {"one argument"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test5() throws Exception {
        String[] args = {"multiple long", "arguments with spaces"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }

    @Test
    public void test6() throws Exception {
        String[] args = {"special\tchars", "line\nbreaks"};
        TestRunner.run(FILENAME, args, "", get(args), "", args.length + 1);
    }
}
