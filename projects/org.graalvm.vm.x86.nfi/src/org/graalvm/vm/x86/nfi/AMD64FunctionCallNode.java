package org.graalvm.vm.x86.nfi;

import java.util.List;
import java.util.logging.Logger;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.InteropCallback;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.posix.SyscallException;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixPointer;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class AMD64FunctionCallNode extends AMD64Node {
    private static final Logger log = Trace.create(AMD64FunctionCallNode.class);

    @Child private InterpreterRootNode root;
    @Child private AMD64ArgumentConversionNode converter = new AMD64ArgumentConversionNode();

    @Child private CallbackNode foreignCall = CallbackNodeGen.create();

    public AMD64FunctionCallNode(ArchitecturalState state) {
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static NativeTypeMirror getType(NativeSignature signature, int i) {
        return signature.getArgTypes().get(i);
    }

    public long execute(VirtualFrame frame, AMD64Function func, Object[] args, List<Object> objects) {
        AMD64Context ctx = getContextReference().get();
        NativeSignature signature = func.getSignature();

        VirtualMemory mem = ctx.getMemory();
        long pname = ctx.getScratchMemory();
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, pname);

        InteropCallback cb = new InteropCallback() {
            public long call(int id, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
                CompilerAsserts.neverPartOfCompilation();

                if (id < 0 || id > objects.size()) {
                    log.warning("Unknown callback: " + id);
                    throw new SyscallException(Errno.ENOSYS);
                }

                if (!(objects.get(id) instanceof Callback)) {
                    log.warning("Unknown callback: " + id);
                    throw new SyscallException(Errno.ENOSYS);
                }

                Callback callback = (Callback) objects.get(id);
                return foreignCall.execute(callback.signature, objects, callback.object, a1, a2, a3, a4, a5, a6);
            }
        };

        long sp = ctx.getSigaltstack();
        long ret = ctx.getReturnAddress();
        long[] rawargs = new long[6];
        for (int i = 0; i < args.length; i++) {
            NativeTypeMirror type = getType(signature, i);
            ConversionResult result = converter.execute(type, ptr, args[i], objects);
            rawargs[i] = result.value;
            ptr = result.ptr;
        }

        long a1 = rawargs[0];
        long a2 = rawargs[1];
        long a3 = rawargs[2];
        long a4 = rawargs[3];
        long a5 = rawargs[4];
        long a6 = rawargs[5];

        ctx.setInteropCallback(cb);

        try {
            return root.executeInterop(frame, sp, ret, func.getFunction(), a1, a2, a3, a4, a5, a6);
        } finally {
            ctx.clearInteropCallback();
        }
    }
}
