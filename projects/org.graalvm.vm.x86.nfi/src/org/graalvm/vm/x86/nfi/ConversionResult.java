package org.graalvm.vm.x86.nfi;

import com.everyware.posix.api.PosixPointer;

public class ConversionResult {
    public final long value;
    public final PosixPointer ptr;

    public ConversionResult(long value, PosixPointer ptr) {
        this.value = value;
        this.ptr = ptr;
    }
}
