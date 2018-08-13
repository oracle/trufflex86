package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = NativePointer.class)
public class NativePointerMessageResolution {
    @Resolve(message = "IS_POINTER")
    abstract static class IsNativePointerNode extends Node {
        public boolean access(@SuppressWarnings("unused") NativePointer receiver) {
            return true;
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsNativePointerNode extends Node {
        public long access(NativePointer receiver) {
            return receiver.value;
        }
    }

    @Resolve(message = "TO_NATIVE")
    abstract static class ToNativePointerNode extends Node {
        public NativePointer access(NativePointer receiver) {
            return receiver;
        }
    }

    @Resolve(message = "UNBOX")
    abstract static class UnboxNativePointerNode extends Node {
        public long access(NativePointer receiver) {
            return receiver.value;
        }
    }

    @Resolve(message = "IS_BOXED")
    abstract static class IsBoxedNativePointerNode extends Node {
        @SuppressWarnings("unused")
        public boolean access(NativePointer receiver) {
            return true;
        }
    }

    @Resolve(message = "IS_NULL")
    abstract static class IsNullNativePointerNode extends Node {
        public boolean access(NativePointer receiver) {
            return receiver.value == 0;
        }
    }

    @CanResolve
    abstract static class CanResolveNativePointerNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof NativePointer;
        }
    }
}
