package org.graalvm.vm.x86;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.everyware.util.log.Trace;

public class Options {
    private static final Logger log = Trace.create(Options.class);

    public static final BooleanOption PRINT_DISPATCH_STATS = new BooleanOption("vmx86.dispatch.stats", false);
    public static final BooleanOption USE_LOOP_NODE = new BooleanOption("vmx86.dispatch.loop", true);
    public static final BooleanOption TRUFFLE_CALLS = new BooleanOption("vmx86.exec.calls", false);

    public static final BooleanOption RDTSC_USE_INSTRUCTION_COUNT = new BooleanOption("vmx86.rdtsc.insncnt", false);

    private static class BooleanOption {
        public final String name;
        public final boolean value;

        public BooleanOption(String name, boolean value) {
            this.name = name;
            this.value = value;
        }
    }

    public static boolean getBoolean(BooleanOption option) {
        return getBoolean(option.name, option.value);
    }

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
