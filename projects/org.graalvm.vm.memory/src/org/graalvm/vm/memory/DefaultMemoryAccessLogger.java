package org.graalvm.vm.memory;

import org.graalvm.vm.memory.util.Stringify;
import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

public class DefaultMemoryAccessLogger implements MemoryAccessListener {
    private static String stringify(int size, long value) {
        switch (size) {
            case 1:
                return Stringify.i8((byte) value);
            case 2:
                return Stringify.i16((short) value);
            case 4:
                return Stringify.i32((int) value);
            case 8:
                return Stringify.i64(value);
            default:
                return null;
        }
    }

    public void logMemoryRead(long address, int size) {
        System.out.printf("Memory access to 0x%016x: read %d bytes\n", address, size);
    }

    public void logMemoryRead(long address, int size, long value) {
        String val = stringify(size, value);
        if (val != null) {
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x, '%s')\n", address, size, value, val);
        } else {
            System.out.printf("Memory access to 0x%016x: read %d bytes (0x%016x)\n", address, size, value);
        }
    }

    public void logMemoryRead(long address, Vector128 value) {
        System.out.printf("Memory access to 0x%016x: read 16 bytes (%s)\n", address, value);
    }

    public void logMemoryRead(long address, Vector256 value) {
        System.out.printf("Memory access to 0x%016x: read 32 bytes (%s)\n", address, value);
    }

    public void logMemoryRead(long address, Vector512 value) {
        System.out.printf("Memory access to 0x%016x: read 64 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, int size, long value) {
        String val = stringify(size, value);
        if (val != null) {
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x, '%s')\n", address, size, value, val);
        } else {
            System.out.printf("Memory access to 0x%016x: write %d bytes (0x%016x)\n", address, size, value);
        }
    }

    public void logMemoryWrite(long address, Vector128 value) {
        System.out.printf("Memory access to 0x%016x: write 16 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, Vector256 value) {
        System.out.printf("Memory access to 0x%016x: write 32 bytes (%s)\n", address, value);
    }

    public void logMemoryWrite(long address, Vector512 value) {
        System.out.printf("Memory access to 0x%016x: write 64 bytes (%s)\n", address, value);
    }
}
