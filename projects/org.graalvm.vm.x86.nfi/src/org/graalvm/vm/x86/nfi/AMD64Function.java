package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.nfi.types.NativeSignature;

public class AMD64Function implements TruffleObject {
    private final String name;
    private final long function;
    private final NativeSignature signature;

    public AMD64Function(String name, long function, NativeSignature signature) {
        this.name = name;
        this.function = function;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public long getFunction() {
        return function;
    }

    public NativeSignature getSignature() {
        return signature;
    }

    public ForeignAccess getForeignAccess() {
        return AMD64FunctionMessageResolutionForeign.ACCESS;
    }
}
