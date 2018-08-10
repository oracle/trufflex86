package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class AMD64Symbol implements TruffleObject {
    private final String name;
    private final long address;

    public AMD64Symbol(String name, long address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public long getAddress() {
        return address;
    }

    public ForeignAccess getForeignAccess() {
        return AMD64SymbolMessageResolutionForeign.ACCESS;
    }
}
