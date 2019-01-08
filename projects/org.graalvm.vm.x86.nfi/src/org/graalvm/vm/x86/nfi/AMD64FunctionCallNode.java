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
import com.oracle.truffle.nfi.types.NativeSimpleType;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror.Kind;

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

        boolean returnFloat = false;

        if (signature.getRetType().getKind() == NativeTypeMirror.Kind.SIMPLE) {
            NativeSimpleTypeMirror type = (NativeSimpleTypeMirror) signature.getRetType();
            if (type.getSimpleType() == NativeSimpleType.DOUBLE || type.getSimpleType() == NativeSimpleType.FLOAT) {
                returnFloat = true;
            }
        }

        VirtualMemory mem = ctx.getMemory();
        long pname = ctx.getScratchMemory();
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, pname);
        long callbacks = ctx.getCallbackMemory();
        PosixPointer callbackptr = new PosixVirtualMemoryPointer(mem, callbacks);
        long envptr = ctx.getInteropFunctionPointers().truffleEnv;

        InteropCallback cb = new InteropCallback() {
            public long call(int id, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
                return call(id, a1, a2, a3, a4, a5, a6, 0, 0, 0, 0, 0, 0, 0, 0);
            }

            public long call(int id, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5, long f6, long f7, long f8) throws SyscallException {
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
                long result = foreignCall.execute(callback.signature, objects, callback.object, a1, a2, a3, a4, a5, a6, f1, f2, f3, f4, f5, f6, f7, f8);
                return result;
            }
        };

        long sp = ctx.getSigaltstack();
        long ret = ctx.getReturnAddress();
        long[] rawargs = new long[6];
        long[] floatargs = new long[8];
        int intidx = 0;
        int floatidx = 0;
        int argidx = 0;
        for (int i = 0; i < signature.getFixedArgCount(); i++) {
            NativeTypeMirror type = getType(signature, i);
            if (type.getKind() == Kind.ENV) {
                rawargs[intidx++] = converter.execute(type, ptr, null, null, callbackptr, envptr).value;
            } else {
                ConversionResult result = converter.execute(type, ptr, args[argidx++], objects, callbackptr, envptr);
                if (result.isFloat) {
                    floatargs[floatidx++] = result.value;
                } else {
                    rawargs[intidx++] = result.value;
                }
                ptr = result.ptr;
            }
        }

        long a1 = rawargs[0];
        long a2 = rawargs[1];
        long a3 = rawargs[2];
        long a4 = rawargs[3];
        long a5 = rawargs[4];
        long a6 = rawargs[5];

        long f1 = floatargs[0];
        long f2 = floatargs[1];
        long f3 = floatargs[2];
        long f4 = floatargs[3];
        long f5 = floatargs[4];
        long f6 = floatargs[5];
        long f7 = floatargs[6];
        long f8 = floatargs[7];

        ctx.setInteropCallback(cb);

        try {
            return root.executeInterop(frame, sp, ret, func.getFunction(), a1, a2, a3, a4, a5, a6, f1, f2, f3, f4, f5, f6, f7, f8, returnFloat);
        } catch (Throwable t) {
            mem.dump(0x7f0000001000L, 256);
            throw t;
        } finally {
            ctx.clearInteropCallback();
        }
    }
}
