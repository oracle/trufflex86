package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.types.Parser;

@MessageResolution(receiverType = AMD64Symbol.class)
public class AMD64SymbolMessageResolution {
    protected static String str(NativeTypeMirror type) {
        switch (type.getKind()) {
            case SIMPLE:
                return ((NativeSimpleTypeMirror) type).getSimpleType().toString();
            default:
                return type.getKind().toString();
        }
    }

    @Resolve(message = "INVOKE")
    abstract static class BindSymbolNode extends Node {
        @Child private AsStringNode asString = AsStringNodeGen.create(true);

        public TruffleObject access(AMD64Symbol receiver, String name, Object[] args) {
            if (name.equals("bind")) {
                String signature = asString.execute(args[0]);
                NativeSignature parsed = Parser.parseSignature(signature);
                return new AMD64Function(receiver.getName(), receiver.getAddress(), parsed);
            } else {
                CompilerDirectives.transferToInterpreter();
                throw new IllegalArgumentException("unsupported method " + name);
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
