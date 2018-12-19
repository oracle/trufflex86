package org.graalvm.vm.x86.nfi;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
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

@MessageResolution(receiverType = AMD64Function.class)
public class AMD64FunctionMessageResolution {
    @Resolve(message = "EXECUTE")
    abstract static class LookupSymbolNode extends Node {
        private RootCallTarget execute = Truffle.getRuntime().createCallTarget(createCallTarget());
        @Child private AsStringNode asString = AsStringNodeGen.create(true);
        @Child private NativeTypeConversionNode converter = new NativeTypeConversionNode();

        private static AMD64FunctionCallTarget createCallTarget() {
            AMD64Context ctx = AMD64NFILanguage.getCurrentContextReference().get();
            return new AMD64FunctionCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
        }

        public Object access(AMD64Function receiver, Object[] args) {
            NativeSignature signature = receiver.getSignature();
            List<Object> objects = new ArrayList<>();
            long result = (long) execute.call(new Object[]{receiver, args, objects});
            return converter.execute(signature.getRetType(), result, objects);
        }
    }

    @Resolve(message = "IS_EXECUTABLE")
    abstract static class IsExecutableNode extends Node {
        @SuppressWarnings("unused")
        public boolean access(AMD64Function receiver) {
            return true;
        }
    }

    @Resolve(message = "INVOKE")
    abstract static class BindSymbolNode extends Node {
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        @TruffleBoundary
        private static NativeSignature parseSignature(String signature) {
            return Parser.parseSignature(signature);
        }

        public TruffleObject access(AMD64Function receiver, String name, Object[] args) {
            if (!"bind".equals(name)) {
                throw UnknownIdentifierException.raise(name);
            }
            if (args.length != 1) {
                throw ArityException.raise(1, args.length);
            }

            String signature = asString.execute(args[0]);
            NativeSignature parsed = parseSignature(signature);
            return new AMD64Function(receiver.getName(), receiver.getFunction(), parsed);
        }
    }

    @Resolve(message = "KEYS")
    abstract static class LibFFIFunctionKeysNode extends Node {
        private static final KeysArray KEYS = new KeysArray(new String[]{"bind"});

        @SuppressWarnings("unused")
        public TruffleObject access(AMD64Function receiver) {
            return KEYS;
        }
    }

    @Resolve(message = "KEY_INFO")
    abstract static class LibFFIFunctionKeyInfoNode extends Node {
        @Child AsStringNode asString = AsStringNodeGen.create(true);

        @SuppressWarnings("unused")
        public int access(AMD64Function receiver, Object identifier) {
            if ("bind".equals(asString.execute(identifier))) {
                return KeyInfo.INVOCABLE;
            } else {
                return KeyInfo.NONE;
            }
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsNativePointerNode extends Node {
        public long access(AMD64Function receiver) {
            return receiver.getFunction();
        }
    }

    @Resolve(message = "IS_NULL")
    abstract static class IsNullNativePointerNode extends Node {
        public boolean access(AMD64Function receiver) {
            return receiver.getFunction() == 0;
        }
    }

    @CanResolve
    abstract static class CanResolveAMD64FunctionNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof AMD64Function;
        }
    }
}
