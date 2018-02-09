package org.graalvm.vm.x86.test;

import java.io.File;
import java.io.IOException;

public class TestOptions {
    public static final String PATH = getPath();

    // try to find the path for the test suite
    private static String getPath() {
        String path = System.getProperty("vmx86test.testSuitePath");
        if (path != null) {
            return path;
        } else {
            File f = new File("mxbuild/testcases");
            if (f.exists()) {
                try {
                    return f.getCanonicalPath();
                } catch (IOException e) {
                    return null;
                }
            } else {
                f = new File("../../mxbuild/testcases");
                try {
                    return f.getCanonicalPath();
                } catch (IOException e) {
                    return null;
                }
            }
        }
    }
}