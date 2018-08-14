package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class AMD64String implements TruffleObject {
    public final long ptr;

    public AMD64String(long ptr) {
        this.ptr = ptr;
    }

    public ForeignAccess getForeignAccess() {
        return AMD64StringMessageResolutionForeign.ACCESS;
    }
}
