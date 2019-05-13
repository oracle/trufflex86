/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.util.log.Trace;

public class Options {
    private static final Logger log = Trace.create(Options.class);

    public static final BooleanOption STARTUP_BANNER = new BooleanOption("vmx86.startup.banner", false);

    // Debugging options
    public static final BooleanOption DEBUG_EXEC = new BooleanOption("vmx86.debug.exec", false);
    public static final BooleanOption DEBUG_EXEC_TRACE = new BooleanOption("vmx86.debug.exec.trace", false);
    public static final StringOption DEBUG_EXEC_TRACEFILE = new StringOption("vmx86.debug.exec.tracefile", "vmx86.trc");
    public static final BooleanOption DEBUG_PRINT_SYMBOLS = new BooleanOption("vmx86.debug.symbols", true);
    public static final BooleanOption DEBUG_PRINT_STATE = new BooleanOption("vmx86.debug.state", true);
    public static final BooleanOption DEBUG_PRINT_ONCE = new BooleanOption("vmx86.debug.once", false);
    public static final BooleanOption DEBUG_PRINT_ARGS = new BooleanOption("vmx86.debug.args", true);
    public static final BooleanOption DEBUG_SYMBOLS = new BooleanOption("vmx86.debug.symbols", false);
    public static final BooleanOption DEBUG_STATIC_ENV = new BooleanOption("vmx86.debug.staticenv", false);

    // Dispatch logic
    public static final BooleanOption DEBUG_DISPATCH = new BooleanOption("vmx86.debug.dispatch", false);
    public static final BooleanOption SIMPLE_DISPATCH = new BooleanOption("vmx86.debug.simpleDispatch", false);
    public static final BooleanOption PRINT_DISPATCH_STATS = new BooleanOption("vmx86.dispatch.stats", false);
    public static final BooleanOption USE_LOOP_NODE = new BooleanOption("vmx86.dispatch.loop", true);
    public static final BooleanOption TRUFFLE_CALLS = new BooleanOption("vmx86.exec.calls", false);
    public static final BooleanOption TRACE_STATE_CHECK = new BooleanOption("vmx86.exec.check", false);

    // ELF loader
    public static final LongOption LOAD_BIAS = new LongOption("vmx86.elf.load_bias", 0);
    public static final StringOption STACK_CONTENT = new StringOption("vmx86.elf.stack", null);
    public static final StringOption STATIC_BINARY = new StringOption("vmx86.elf.binary", null);

    // Instructions
    public static final BooleanOption RDTSC_USE_INSTRUCTION_COUNT = new BooleanOption("vmx86.rdtsc.insncnt", false);

    // POSIX functions
    public static final BooleanOption USE_STATIC_TIME = new BooleanOption("posix.time.static", false);

    public static final StringOption FSROOT = new StringOption("vmx86.fsroot", null);
    public static final StringOption CWD = new StringOption("vmx86.cwd", null);

    // CPUID
    public static final StringOption CPUID_BRAND = new StringOption("vmx86.cpuid.brand", "VMX86 on Graal/Truffle");
    public static final StringOption VENDOR_ID = new StringOption("vmx86.cpuid.vendor", "VMX86onGraal");

    // Substitutions
    public static final BooleanOption ENABLE_SUBSTITUTIONS = new BooleanOption("vmx86.exec.subst", false);
    public static final BooleanOption TRACE_SUBSTITUTIONS = new BooleanOption("vmx86.exec.subst.trace", false);

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
