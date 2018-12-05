package org.graalvm.vm.x86.nfi;

import com.everyware.posix.api.PosixPointer;

public class ConversionResult {
    public final long value;
    public final PosixPointer ptr;
    public final boolean isFloat;

    public ConversionResult(long value, PosixPointer ptr) {
        this.value = value;
        this.ptr = ptr;
        this.isFloat = false;
    }

    public ConversionResult(float value, PosixPointer ptr) {
        this.value = Integer.toUnsignedLong(Float.floatToRawIntBits(value));
        this.ptr = ptr;
        this.isFloat = true;
    }

    public ConversionResult(double value, PosixPointer ptr) {
        this.value = Double.doubleToRawLongBits(value);
        this.ptr = ptr;
        this.isFloat = true;
    }
}
