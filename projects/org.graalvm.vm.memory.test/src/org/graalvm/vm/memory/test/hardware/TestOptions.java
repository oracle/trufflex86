package org.graalvm.vm.memory.test.hardware;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class TestOptions {
    public static final String PATH = getPath();

    // try to find the path for the native library
    private static String getPath() {
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
        if (!PATH.equals(System.getProperty("java.library.path"))) {
            System.setProperty("java.library.path", PATH);
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        }
    }
}