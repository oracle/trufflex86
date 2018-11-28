package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;
import org.graalvm.vm.x86.posix.InteropErrorException;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.KeyInfo;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = AMD64Library.class)
public class AMD64LibraryMessageResolution {
    @Resolve(message = "READ")
    abstract static class LookupSymbolNode extends Node {
        private RootCallTarget lookup = Truffle.getRuntime().createCallTarget(createSymbolLookupNode());
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        private static AMD64SymbolLookupCallTarget createSymbolLookupNode() {
            AMD64Context ctx = AMD64NFILanguage.getCurrentContextReference().get();
            return new AMD64SymbolLookupCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
        }

        public TruffleObject access(AMD64Library receiver, Object symbol) {
            String symname = asString.execute(symbol);
            long ptr = (long) lookup.call(new Object[]{receiver, symname});
            return new AMD64Symbol(symname, ptr);
        }
    }

    @Resolve(message = "KEY_INFO")
    abstract static class KeyInfoNode extends Node {
        private RootCallTarget lookup = Truffle.getRuntime().createCallTarget(createSymbolLookupNode());
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        private static AMD64SymbolLookupCallTarget createSymbolLookupNode() {
            AMD64Context ctx = AMD64NFILanguage.getCurrentContextReference().get();
            return new AMD64SymbolLookupCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
        }

        public int access(AMD64Library receiver, Object symbol) {
            String symname = asString.execute(symbol);
            try {
                long ptr = (long) lookup.call(new Object[]{receiver, symname});
                if (ptr == 0) {
                    return KeyInfo.NONE;
                } else {
                    return KeyInfo.READABLE;
                }
            } catch (InteropErrorException e) {
                return KeyInfo.NONE;
            }
        }
    }

    @CanResolve
    abstract static class CanResolveAMD64LibraryNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof AMD64Library;
        }
    }
}
