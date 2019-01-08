package org.graalvm.vm.x86.nfi;

import java.util.List;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleType;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror.Kind;

public class Util {
    @TruffleBoundary
    public static PosixPointer strcpy(PosixPointer dst, String src) {
        return CString.strcpy(dst, src);
    }

    @TruffleBoundary
    public static int add(List<Object> objects, Object object) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) == object) {
                return i;
            }
        }
        int id = objects.size();
        objects.add(object);
        return id;
    }

    @TruffleBoundary
    public static Object get(List<Object> list, int id) {
        return list.get(id);
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
    public static boolean isFloatCallback(NativeSignature signature) {
        List<NativeTypeMirror> types = signature.getArgTypes();
        for (NativeTypeMirror type : types) {
            if (type.getKind() == Kind.SIMPLE) {
                NativeSimpleTypeMirror t = (NativeSimpleTypeMirror) type;
                if (t.getSimpleType() == NativeSimpleType.FLOAT || t.getSimpleType() == NativeSimpleType.DOUBLE) {
                    return true;
                }
            }
        }
        return false;
    }
}
