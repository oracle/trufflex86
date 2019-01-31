package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.PosixPointer;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = AMD64String.class)
public class AMD64StringMessageResolution {
    protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    @Resolve(message = "UNBOX")
    abstract static class UnboxNode extends Node {
        public String access(AMD64String receiver) {
            if (receiver.ptr == 0) {
                return null;
            } else {
                VirtualMemory mem = AMD64NFILanguage.getCurrentContextReference().get().getMemory();
                PosixPointer p = new PosixVirtualMemoryPointer(mem, receiver.ptr);
                return CString.cstr(p);
            }
        }
    }

    @Resolve(message = "IS_BOXED")
    abstract static class IsBoxedNode extends Node {
        public boolean access(@SuppressWarnings("unused") AMD64String receiver) {
            return true;
        }
    }

    @Resolve(message = "IS_NULL")
    abstract static class IsNullNode extends Node {
        public boolean access(AMD64String receiver) {
            return receiver.ptr == 0;
        }
    }

    @Resolve(message = "IS_POINTER")
    abstract static class IsPointerNode extends Node {
        public boolean access(@SuppressWarnings("unused") AMD64String receiver) {
            return true;
        }
    }

    @Resolve(message = "TO_NATIVE")
    abstract static class ToNativeNode extends Node {
        public NativePointer access(AMD64String receiver) {
            return new NativePointer(receiver.ptr);
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsPointerNode extends Node {
        public long access(AMD64String receiver) {
            if (MEM_MAP_NATIVE) {
                VirtualMemory mem = AMD64NFILanguage.getCurrentContextReference().get().getMemory();
                return mem.getNativeAddress(receiver.ptr);
            } else {
                return receiver.ptr;
            }
        }
    }

    @CanResolve
    abstract static class CanResolveAMD64StringNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof AMD64String;
        }
    }
}
