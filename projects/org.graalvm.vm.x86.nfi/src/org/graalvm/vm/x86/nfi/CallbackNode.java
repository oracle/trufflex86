package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;
import org.graalvm.vm.x86.nfi.TypeConversion.AsF32Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsF64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsI64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF32NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsI64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;

import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleType;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror.Kind;

public abstract class CallbackNode extends Node {
    public abstract long execute(NativeSignature signature, List<Object> objects, TruffleObject obj, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5,
                    long f6, long f7, long f8);

    @Child private NativeTypeConversionNode argConverter = new NativeTypeConversionNode();

    @Child private AsPointerNode asPointer = AsPointerNodeGen.create();
    @Child private AsI64Node asI64 = AsI64NodeGen.create();
    @Child private AsF32Node asF32 = AsF32NodeGen.create();
    @Child private AsF64Node asF64 = AsF64NodeGen.create();

    @TruffleBoundary
    protected static int getArgCount(NativeSignature signature) {
        int envcnt = 0;
        for (NativeTypeMirror type : signature.getArgTypes()) {
            if (type.getKind() == Kind.ENV) {
                envcnt++;
            }
        }
        return signature.getFixedArgCount() - envcnt;
    }

    @Specialization
    protected long execute(NativeSignature signature, List<Object> objects, TruffleObject obj, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5,
                    long f6, long f7, long f8, @Cached("createExecute()") Node execute) {
        AMD64Context ctx = AMD64Language.getCurrentContextReference().get();
        VirtualMemory mem = ctx.getMemory();
        long callbacks = ctx.getCallbackMemory();
        PosixVirtualMemoryPointer callbacksptr = new PosixVirtualMemoryPointer(mem, callbacks);

        long[] iargs = {a1, a2, a3, a4, a5, a6};
        long[] fargs = {f1, f2, f3, f4, f5, f6, f7, f8};

        Object[] args = new Object[getArgCount(signature)];

        int iidx = 0;
        int fidx = 0;
        int idx = 0;
        for (int i = 0; i < signature.getFixedArgCount(); i++) {
            NativeTypeMirror type = Util.getType(signature, i);
            long raw;
            if (type.getKind() == Kind.SIMPLE) {
                NativeSimpleTypeMirror t = (NativeSimpleTypeMirror) type;
                if (t.getSimpleType() == NativeSimpleType.FLOAT || t.getSimpleType() == NativeSimpleType.DOUBLE) {
                    raw = fargs[fidx++];
                } else {
                    raw = iargs[iidx++];
                }
            } else if (type.getKind() == Kind.ENV) {
                iidx++;
                continue;
            } else {
                raw = iargs[iidx++];
            }
            args[idx++] = argConverter.execute(type, raw, objects);
        }

        try {
            Object result = ForeignAccess.sendExecute(execute, obj, args);
            switch (signature.getRetType().getKind()) {
                case SIMPLE: {
                    NativeSimpleTypeMirror type = (NativeSimpleTypeMirror) signature.getRetType();
                    switch (type.getSimpleType()) {
                        case UINT8:
                        case SINT8:
                        case UINT16:
                        case SINT16:
                        case UINT32:
                        case SINT32:
                        case UINT64:
                        case SINT64:
                            return asI64.execute(result);
                        case POINTER:
                            return asPointer.execute(result).value;
                        case OBJECT:
                            return Util.getObject(objects, result);
                        case VOID:
                            return 0;
                        case FLOAT:
                            return Float.floatToRawIntBits(asF32.execute(result));
                        case DOUBLE:
                            return Double.doubleToRawLongBits(asF64.execute(result));
                        default:
                            CompilerDirectives.transferToInterpreter();
                            throw new AssertionError("unsupported type: " + type.getSimpleType());
                    }
                }
                case FUNCTION: {
                    NativeSignature sig = ((NativeFunctionTypeMirror) signature.getRetType()).getSignature();
                    Callback cb = new Callback(sig, (TruffleObject) result);
                    boolean fret = false;
                    if (sig.getRetType().getKind() == Kind.SIMPLE) {
                        NativeSimpleTypeMirror rettype = (NativeSimpleTypeMirror) sig.getRetType();
                        fret = rettype.getSimpleType() == NativeSimpleType.FLOAT || rettype.getSimpleType() == NativeSimpleType.DOUBLE;
                    }
                    int id = Util.add(objects, cb);
                    long callback = CallbackCode.getCallbackAddress(callbacksptr.getAddress(), id);
                    PosixPointer callbackdata = CallbackCode.getCallbackDataPointer(callbacksptr, id);
                    callbackdata.setI16((short) id);
                    boolean isFloat = fret || Util.isFloatCallback(sig);
                    callbackdata.add(2).setI8((byte) (isFloat ? 1 : 0));
                    return callback;
                }
                default:
                    CompilerDirectives.transferToInterpreter();
                    throw new AssertionError("unsupported type: " + signature.getRetType().getKind());
            }
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw e.raise();
        } catch (UnsupportedTypeException e) {
            CompilerDirectives.transferToInterpreter();
            throw e.raise();
        } catch (ArityException e) {
            CompilerDirectives.transferToInterpreter();
            throw e.raise();
        }
    }

    protected static Node createExecute() {
        return Message.EXECUTE.createNode();
    }
}
