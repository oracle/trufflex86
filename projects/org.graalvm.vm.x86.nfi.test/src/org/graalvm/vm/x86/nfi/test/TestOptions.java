package org.graalvm.vm.x86.nfi.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class TestOptions {
    public static final String PATH = getPath();

    public static final String LIB_PATH = getLibPath();

    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        } else {
            initialized = true;
        }
        try {
            setLibraryPath();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

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

    // try to find the path for the native library
    private static String getLibPath() {
        String path = System.getProperty("java.library.path");
        if (path != null && path.contains("/build")) {
            return path;
        } else {
            File f = new File("build");
            if (f.exists()) {
                try {
                    if (path != null) {
                        return path + ":" + f.getCanonicalPath();
                    } else {
                        return f.getCanonicalPath();
                    }
                } catch (IOException e) {
                    return null;
                }
            } else {
                f = new File("../../build");
                try {
                    if (path != null) {
                        return path + ":" + f.getCanonicalPath();
                    } else {
                        return f.getCanonicalPath();
                    }
                } catch (IOException e) {
                    return null;
                }
            }
        }
    }

    public static void setLibraryPath() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (!LIB_PATH.equals(System.getProperty("java.library.path"))) {
            System.setProperty("java.library.path", LIB_PATH);
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        }
    }
}