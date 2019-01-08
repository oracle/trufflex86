package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.VirtualMemory;

public class TruffleNativeAPI {
    public static final byte[] CODE = {
                    // getTruffleContext
                    /* 00 */ 0x31, (byte) 0xc0,              // xor eax,eax
                    /* 02 */ (byte) 0xc3,                    // ret

                    // newObjectRef
                    /* 03 */ 0x48, (byte) 0x89, (byte) 0xf0, // mov rax,rsi
                    /* 06 */ (byte) 0xc3,                    // ret

                    // releaseObjectRef
                    /* 07 */ 0x48, (byte) 0x89, (byte) 0xf0, // mov rax,rsi
                    /* 0a */ (byte) 0xc3,                    // ret

                    // releaseAndReturn
                    /* 0b */ 0x48, (byte) 0x89, (byte) 0xf0, // mov rax,rsi
                    /* 0e */ (byte) 0xc3,                    // ret

                    // isSameObject
                    /* 0f */ 0x31, (byte) 0xc0,              // xor eax,eax
                    /* 11 */ 0x48, (byte) 0x85, (byte) 0xf2, // test rdx,rsi
                    /* 14 */ 0x75, 0x02,                     // jne 18 <isSameObject+0x9>
                    /* 16 */ (byte) 0xff, (byte) 0xc0,       // inc eax
                    /* 18 */ (byte) 0xc3,                    // ret

                    // newClosureRef
                    /* 19 */ 0x31, (byte) 0xc0,              // xor eax,eax
                    /* 1b */ (byte) 0xc3,                    // ret

                    // releaseClosureRef
                    /* 1c */ 0x31, (byte) 0xc0,              // xor eax,eax
                    /* 1e */ (byte) 0xc3,                    // ret

                    // getClosureObject
                    /* 1f */ 0x31, (byte) 0xc0,              // xor eax,eax
                    /* 21 */ (byte) 0xc3,                    // ret
    };

    public static final int[] OFFSETS = {0x00, 0x03, 0x07, 0x0b, 0x0f, 0x19, 0x1c, 0x1f};

    public static void writeCode(VirtualMemory mem, long ptr) {
        for (int i = 0; i < CODE.length; i++) {
            mem.setI8(ptr + i, CODE[i]);
        }
    }

    public static void writeStruct(VirtualMemory mem, long ptr, long codeptr) {
        for (int i = 0; i < OFFSETS.length; i++) {
            mem.setI64(ptr + i * 8, OFFSETS[i] + codeptr);
        }
    }
}
