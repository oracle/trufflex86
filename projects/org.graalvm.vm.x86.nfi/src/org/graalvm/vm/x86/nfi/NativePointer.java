package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class NativePointer implements TruffleObject {
    public final long value;

    public NativePointer(long value) {
        this.value = value;
    }

    public ForeignAccess getForeignAccess() {
        return NativePointerMessageResolutionForeign.ACCESS;
    }
}
