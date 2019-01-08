package org.graalvm.vm.x86.nfi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.nfi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class NativeSignatureEncoder {
    @TruffleBoundary
    public static String encode(NativeSignature signature) {
        List<NativeTypeMirror> args = signature.getArgTypes();
        List<String> parts = new ArrayList<>();
        for (NativeTypeMirror type : args) {
            parts.add(encodeType(type));
        }
        return "(" + parts.stream().collect(Collectors.joining(",")) + "):" + encodeType(signature.getRetType());
    }

    @TruffleBoundary
    public static String encodeType(NativeTypeMirror type) {
        switch (type.getKind()) {
            case ENV:
                return "ENV";
            case FUNCTION:
                return "(" + encode(((NativeFunctionTypeMirror) type).getSignature()) + ")";
            case SIMPLE: {
                NativeSimpleTypeMirror simple = (NativeSimpleTypeMirror) type;
                switch (simple.getSimpleType()) {
                    case UINT8:
                        return "UINT8";
                    case SINT8:
                        return "SINT8";
                    case UINT16:
                        return "UINT16";
                    case SINT16:
                        return "SINT16";
                    case UINT32:
                        return "UINT32";
                    case SINT32:
                        return "SINT32";
                    case UINT64:
                        return "UINT64";
                    case SINT64:
                        return "SINT64";
                    case FLOAT:
                        return "FLOAT";
                    case DOUBLE:
                        return "DOUBLE";
                    case STRING:
                        return "STRING";
                    case OBJECT:
                        return "OBJECT";
                    case POINTER:
                        return "POINTER";
                    case VOID:
                        return "VOID";
                    default:
                        return "???";
                }
            }
            default:
                return "???";
        }
    }
}
