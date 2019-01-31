package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.PosixPointer;

public class CallbackCode {
    public static final long DATA_OFFSET = 3584;

    // @formatter:off
    private static final byte[] CALLBACK = {
            /* 00 */  0x49, (byte) 0xba, 0x00, 0x00, 0x00, 0x00, 0x00,  // movabs r10,0x0000000000000000
            /* 07 */  0x00, 0x00, 0x00,
            /* 0a */  0x41, (byte) 0x8a, (byte) 0x42, (byte) 0x02,      // mov    al,BYTE PTR [r10+0x2]
            /* 0e */  (byte) 0x84, (byte) 0xc0,                         // test   al,al
            /* 10 */  0x75, 0x03,                                       // jne    15 <loc+0x15>
            /* 12 */  (byte) 0xf8,                                      // clc
            /* 13 */  (byte) 0xeb, 0x01,                                // jmp    16 <loc+0x16>
            /* 15 */  (byte) 0xf9,                                      // stc
            /* 16 */  0x66, 0x41, (byte) 0x8b, 0x02,                    // mov    ax,WORD PTR [r10]
            /* 1a */  (byte) 0xf1,                                      // icebp
            /* 1b */  (byte) 0xc3                                       // ret
    };
    // @formatter:on

    public static long getCallbackAddress(long ptr, int id) {
        return ptr + id * CALLBACK.length;
    }

    public static long getCallbackDataAddress(long ptr, int id) {
        return ptr + DATA_OFFSET + id * 4;
    }

    public static PosixPointer getCallbackDataPointer(PosixPointer ptr, int id) {
        return ptr.add((int) (DATA_OFFSET + id * 4));
    }

    public static void writeCallback(VirtualMemory mem, long ptr, int id) {
        long base = getCallbackAddress(ptr, id);
        long data = getCallbackDataAddress(ptr, id);
        for (int i = 0; i < 2; i++) {
            mem.setI8(base + i, CALLBACK[i]);
        }
        for (int i = 10; i < CALLBACK.length; i++) {
            mem.setI8(base + i, CALLBACK[i]);
        }
        mem.setI64(base + 2, data);
    }
}
