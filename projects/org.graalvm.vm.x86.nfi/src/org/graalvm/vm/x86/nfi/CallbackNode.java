package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.x86.nfi.TypeConversion.AsF32Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsF64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsI64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF32NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsI64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;

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
    protected static Object get(List<Object> list, int index) {
        return list.get(index);
    }

    @TruffleBoundary
    protected static long getObject(List<Object> list, Object object) {
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o == object) {
                return MagicValues.encodeObject(i);
            }
        }
        int i = list.size();
        list.add(object);
        return MagicValues.encodeObject(i);
    }

    @TruffleBoundary
    protected static int size(List<Object> list) {
        return list.size();
    }

    @TruffleBoundary
    protected static NativeTypeMirror getType(NativeSignature signature, int id) {
        return signature.getArgTypes().get(id);
    }

    @TruffleBoundary
    protected static int getArgCount(NativeSignature signature) {
        return signature.getFixedArgCount();
    }

    @Specialization
    protected long execute(NativeSignature signature, List<Object> objects, TruffleObject obj, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5,
                    long f6, long f7, long f8, @Cached("createExecute()") Node execute) {
        long[] iargs = {a1, a2, a3, a4, a5, a6};
        long[] fargs = {f1, f2, f3, f4, f5, f6, f7, f8};

        Object[] args = new Object[getArgCount(signature)];

        int iidx = 0;
        int fidx = 0;
        for (int i = 0; i < args.length; i++) {
            NativeTypeMirror type = getType(signature, i);
            long raw;
            if (type.getKind() == Kind.SIMPLE) {
                NativeSimpleTypeMirror t = (NativeSimpleTypeMirror) type;
                if (t.getSimpleType() == NativeSimpleType.FLOAT || t.getSimpleType() == NativeSimpleType.DOUBLE) {
                    raw = fargs[fidx++];
                } else {
                    raw = iargs[iidx++];
                }
            } else {
                raw = iargs[iidx++];
            }
            args[i] = argConverter.execute(type, raw, objects);
        }

        try {
            Object result = ForeignAccess.sendExecute(execute, obj, args);
            if (signature.getRetType().getKind() == NativeTypeMirror.Kind.SIMPLE) {
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
                        return asPointer.execute((TruffleObject) result).value;
                    case OBJECT:
                        return getObject(objects, result);
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
            } else {
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
