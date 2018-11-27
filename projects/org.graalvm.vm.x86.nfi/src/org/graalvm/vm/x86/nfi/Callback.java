package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.nfi.types.NativeSignature;

public class Callback {
    public final NativeSignature signature;
    public final TruffleObject object;

    public Callback(NativeSignature signature, TruffleObject object) {
        this.signature = signature;
        this.object = object;
    }

    @Override
    public String toString() {
        return "Callback[" + signature + "," + object + "]";
    }
}
