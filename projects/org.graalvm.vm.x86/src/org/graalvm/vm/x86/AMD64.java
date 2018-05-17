package org.graalvm.vm.x86;

public class AMD64 {
    public static final long STACK_SIZE = 8 * 1024 * 1024; // 8M
    // public static final long STACK_ADDRESS = 0x7fff6c845000L;
    // public static final long STACK_ADDRESS = 0xf6fff000L;
    public static final long STACK_ADDRESS = 0x0000800000000000L;
    public static final long STACK_BASE = STACK_ADDRESS - STACK_SIZE;

    public static final int DCACHE_LINE_SIZE = 0x20;
    public static final int ICACHE_LINE_SIZE = 0x20;
}
