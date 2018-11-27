package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.x86.nfi.TypeConversion.AsI64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
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
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public abstract class CallbackNode extends Node {
    public abstract long execute(NativeSignature signature, List<Object> objects, TruffleObject obj, long a1, long a2, long a3, long a4, long a5, long a6);

    @Child private NativeTypeConversionNode argConverter = new NativeTypeConversionNode();

    @Child private AsPointerNode asPointer = AsPointerNodeGen.create();
    @Child private AsI64Node asI64 = AsI64NodeGen.create();

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
    public long execute(NativeSignature signature, List<Object> objects, TruffleObject obj, long a1, long a2, long a3, long a4, long a5, long a6, @Cached("createExecute()") Node execute) {
        long[] rawargs = new long[getArgCount(signature)];
        int argcnt = rawargs.length;
        if (argcnt > 6) {
            argcnt = 6;
        }

        // NOTE: fallthrough is intended!
        switch (argcnt) {
            case 6:
                rawargs[5] = a6;
            case 5:
                rawargs[4] = a5;
            case 4:
                rawargs[3] = a4;
            case 3:
                rawargs[2] = a3;
            case 2:
                rawargs[1] = a2;
            case 1:
                rawargs[0] = a1;
        }

        Object[] args = new Object[rawargs.length];

        for (int i = 0; i < argcnt; i++) {
            NativeTypeMirror type = getType(signature, i);
            args[i] = argConverter.execute(type, rawargs[i], objects);
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
