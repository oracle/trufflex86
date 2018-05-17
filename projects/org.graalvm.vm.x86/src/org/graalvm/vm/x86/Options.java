package org.graalvm.vm.x86;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.everyware.util.log.Trace;

public class Options {
    private static final Logger log = Trace.create(Options.class);

    public static boolean getBoolean(String name, boolean fallback) {
        String value = System.getProperty(name, Boolean.toString(fallback));
        return value.equalsIgnoreCase("true") || value.equals("1");
    }

    public static long getLong(String name, long fallback) {
        String value = System.getProperty(name, Long.toString(fallback));
        try {
            if (value.startsWith("0x")) {
                return Long.parseUnsignedLong(value.substring(2), 16);
            } else {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "Invalid value \"" + value + "\" for option \"" + name + "\". Using default value " + fallback);
            return fallback;
        }
    }

    public static String getString(String name) {
        return getString(name, null);
    }

    public static String getString(String name, String fallback) {
        return System.getProperty(name, fallback);
    }
}
