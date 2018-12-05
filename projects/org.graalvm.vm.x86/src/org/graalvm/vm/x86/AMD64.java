package org.graalvm.vm.x86;

public class AMD64 {
    public static final long STACK_SIZE = 8 * 1024 * 1024; // 8M
    public static final long STACK_ADDRESS_DBG = 0x7fff6c845000L;
    public static final long STACK_ADDRESS_NATIVE = 0x1ffffffff000L;
    // public static final long STACK_ADDRESS = 0xf6fff000L;
    // public static final long STACK_ADDRESS = 0x0000800000000000L;
    public static final long STACK_ADDRESS = Options.getBoolean(Options.DEBUG_STATIC_ENV) ? STACK_ADDRESS_DBG : STACK_ADDRESS_NATIVE;
    public static final long STACK_BASE = STACK_ADDRESS - STACK_SIZE;

    public static final int DCACHE_LINE_SIZE = 0x20;
    public static final int ICACHE_LINE_SIZE = 0x20;

    public static final long SCRATCH_SIZE = 4 * 1024 * 1024; // 4MB

    public static final long RETURN_BASE = STACK_BASE - 16384;

    // @formatter:off
    public static final byte[] RETURN_CODE = {
                    0x48, (byte) 0x89, (byte) 0xc7,                     // mov    rdi,rax
                    (byte) 0xb8, 0x02, 0x00, (byte) 0xde, (byte) 0xc0,  // mov    eax,0xc0de0002
                    0x0f, 0x05,                                         // syscall
    };
    // @formatter:on
}
