package org.graalvm.vm.memory;

public class MemoryOptions {
    public static final BooleanOption MEM_DEBUG = new BooleanOption("mem.debug", false);
    public static final BooleanOption MEM_VIRTUAL = new BooleanOption("mem.virtual", false);
    public static final BooleanOption MEM_VERIFY = new BooleanOption("mem.verify", false);

    public static final BooleanOption BYPASS_SEGFAULT_CHECK = new BooleanOption("mem.native.bypasssegfaults", false);

    public static class BooleanOption {
        public final String name;
        public final boolean value;

        private BooleanOption(String name, boolean value) {
            this.name = name;
            this.value = value;
        }

        public boolean get() {
            return getBoolean(this);
        }
    }

    public static boolean getBoolean(BooleanOption option) {
        return getBoolean(option.name, option.value);
    }

    public static boolean getBoolean(String name, boolean fallback) {
        String value = System.getProperty(name, Boolean.toString(fallback));
        return value.equalsIgnoreCase("true") || value.equals("1");
    }
}
