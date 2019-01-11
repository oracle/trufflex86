package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = NativePointer.class)
public class NativePointerMessageResolution {
    protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    @Resolve(message = "IS_POINTER")
    abstract static class IsNativePointerNode extends Node {
        public boolean access(@SuppressWarnings("unused") NativePointer receiver) {
            return true;
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsNativePointerNode extends Node {
        public long access(NativePointer receiver) {
            if (MEM_MAP_NATIVE) {
                if (receiver.value < 0) {
                    return VirtualMemory.fromMappedNative(receiver.value);
                } else {
                    VirtualMemory mem = AMD64NFILanguage.getCurrentContextReference().get().getMemory();
                    return mem.getNativeAddress(receiver.value);
                }
            }
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
