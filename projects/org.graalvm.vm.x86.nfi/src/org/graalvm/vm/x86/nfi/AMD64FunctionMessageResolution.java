package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeSignature;

@MessageResolution(receiverType = AMD64Function.class)
public class AMD64FunctionMessageResolution {
    @Resolve(message = "EXECUTE")
    abstract static class LookupSymbolNode extends Node {
        private RootCallTarget lookup = Truffle.getRuntime().createCallTarget(createCallTarget());
        @Child private AsStringNode asString = AsStringNodeGen.create(true);
        @Child private NativeTypeConversionNode converter = new NativeTypeConversionNode();

        private static AMD64FunctionCallTarget createCallTarget() {
            AMD64Context ctx = AMD64NFILanguage.getCurrentContextReference().get();
            return new AMD64FunctionCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
        }

        public Object access(AMD64Function receiver, Object[] args) {
            NativeSignature signature = receiver.getSignature();
            long result = (long) lookup.call(new Object[]{receiver, args});
            return converter.execute(signature.getRetType(), result);
        }
    }

    @CanResolve
    abstract static class CanResolveAMD64FunctionNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof AMD64Function;
        }
    }
}
