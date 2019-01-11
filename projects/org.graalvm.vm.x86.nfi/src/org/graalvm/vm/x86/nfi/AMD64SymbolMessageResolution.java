package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.KeyInfo;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.Parser;

@MessageResolution(receiverType = AMD64Symbol.class)
public class AMD64SymbolMessageResolution {
    protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    @TruffleBoundary
    private static NativeSignature parseSignature(String signature) {
        return Parser.parseSignature(signature);
    }

    @Resolve(message = "INVOKE")
    abstract static class BindSymbolNode extends Node {
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        public TruffleObject access(AMD64Symbol receiver, String name, Object[] args) {
            if (!"bind".equals(name)) {
                throw UnknownIdentifierException.raise(name);
            }
            if (args.length != 1) {
                throw ArityException.raise(1, args.length);
            }

            String signature = asString.execute(args[0]);
            NativeSignature parsed = parseSignature(signature);
            return new AMD64Function(receiver.getName(), receiver.getAddress(), parsed);
        }
    }

    @Resolve(message = "KEYS")
    abstract static class KeysNode extends Node {
        private static final KeysArray KEYS = new KeysArray(new String[]{"bind"});

        @SuppressWarnings("unused")
        public TruffleObject access(AMD64Symbol receiver) {
            return KEYS;
        }
    }

    @Resolve(message = "KEY_INFO")
    abstract static class KeyInfoNode extends Node {
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        @SuppressWarnings("unused")
        public int access(AMD64Symbol receiver, Object arg) {
            String identifier = asString.execute(arg);
            if ("bind".equals(identifier)) {
                return KeyInfo.INVOCABLE;
            } else {
                return KeyInfo.NONE;
            }
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsNativePointerNode extends Node {
        public long access(AMD64Symbol receiver) {
            if (MEM_MAP_NATIVE) {
                VirtualMemory mem = AMD64NFILanguage.getCurrentContextReference().get().getMemory();
                return mem.getNativeAddress(receiver.getAddress());
            } else {
                return receiver.getAddress();
            }
        }
    }

    @CanResolve
    abstract static class CanResolveAMD64SymbolNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof AMD64Symbol;
        }
    }
}
