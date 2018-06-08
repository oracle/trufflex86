package org.graalvm.vm.x86;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.everyware.util.log.Trace;

public class Options {
    private static final Logger log = Trace.create(Options.class);

    public static final BooleanOption STARTUP_BANNER = new BooleanOption("vmx86.startup.banner", false);

    // Debugging options
    public static final BooleanOption DEBUG_EXEC = new BooleanOption("vmx86.debug.exec", false);
    public static final BooleanOption DEBUG_PRINT_SYMBOLS = new BooleanOption("vmx86.debug.symbols", true);
    public static final BooleanOption DEBUG_PRINT_STATE = new BooleanOption("vmx86.debug.state", true);
    public static final BooleanOption DEBUG_PRINT_ONCE = new BooleanOption("vmx86.debug.once", false);
    public static final BooleanOption DEBUG_PRINT_ARGS = new BooleanOption("vmx86.debug.args", true);

    // Dispatch logic
    public static final BooleanOption SIMPLE_DISPATCH = new BooleanOption("vmx86.debug.simpleDispatch", false);
    public static final BooleanOption PRINT_DISPATCH_STATS = new BooleanOption("vmx86.dispatch.stats", false);
    public static final BooleanOption USE_LOOP_NODE = new BooleanOption("vmx86.dispatch.loop", true);
    public static final BooleanOption TRUFFLE_CALLS = new BooleanOption("vmx86.exec.calls", false);

    // ELF loader
    public static final LongOption LOAD_BIAS = new LongOption("vmx86.elf.load_bias", 0);
    public static final StringOption STACK_CONTENT = new StringOption("vmx86.elf.stack", null);

    // Instructions
    public static final BooleanOption RDTSC_USE_INSTRUCTION_COUNT = new BooleanOption("vmx86.rdtsc.insncnt", false);

    // CPUID
    public static final StringOption CPUID_BRAND = new StringOption("vmx86.cpuid.brand", "VMX86 on Graal/Truffle");
    public static final StringOption VENDOR_ID = new StringOption("vmx86.cpuid.vendor", "VMX86onGraal");

    private static class BooleanOption {
        public final String name;
        public final boolean value;

        public BooleanOption(String name, boolean value) {
            this.name = name;
            this.value = value;
        }
    }

    private static class StringOption {
        public final String name;
        public final String value;

        public StringOption(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private static class LongOption {
        public final String name;
        public final long value;

        public LongOption(String name, long value) {
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

    public static long getLong(LongOption option) {
        return getLong(option.name, option.value);
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

    public static String getString(StringOption option) {
        return getString(option.name, option.value);
    }

    public static String getString(String name) {
        return getString(name, null);
    }

    public static String getString(String name, String fallback) {
        return System.getProperty(name, fallback);
    }
}
