package org.graalvm.vm.memory;

import org.graalvm.vm.memory.vector.Vector128;
import org.graalvm.vm.memory.vector.Vector256;
import org.graalvm.vm.memory.vector.Vector512;

public interface MemoryAccessListener {
    void logMemoryRead(long address, int size);

    void logMemoryRead(long address, int size, long value);

    void logMemoryRead(long address, Vector128 value);

    void logMemoryRead(long address, Vector256 value);

    void logMemoryRead(long address, Vector512 value);

    void logMemoryWrite(long address, int size, long value);

    void logMemoryWrite(long address, Vector128 value);

    void logMemoryWrite(long address, Vector256 value);

    void logMemoryWrite(long address, Vector512 value);
}
