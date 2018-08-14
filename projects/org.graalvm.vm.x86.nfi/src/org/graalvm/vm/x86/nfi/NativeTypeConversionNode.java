package org.graalvm.vm.x86.nfi;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class NativeTypeConversionNode extends Node {
    public Object execute(NativeTypeMirror type, long value) {
        switch (type.getKind()) {
            case SIMPLE: {
                NativeSimpleTypeMirror mirror = (NativeSimpleTypeMirror) type;
                switch (mirror.getSimpleType()) {
                    case SINT8:
                    case UINT8:
                        return (byte) value;
                    case SINT16:
                    case UINT16:
                        return (short) value;
                    case SINT32:
                    case UINT32:
                        return (int) value;
                    case SINT64:
                    case UINT64:
                        return value;
                    case POINTER:
                        return new NativePointer(value);
                    case STRING:
                        return new AMD64String(value);
                    case OBJECT:
                        if (value == 0) {
                            return new NativePointer(value);
                        } else {
                            CompilerDirectives.transferToInterpreter();
                            throw new AssertionError("Unsupported type: " + mirror.getSimpleType());
                        }
                    case VOID:
                        return new NativePointer(0);
                    default:
                        CompilerDirectives.transferToInterpreter();
                        throw new AssertionError("Unsupported type: " + mirror.getSimpleType());
                }
            }
            case FUNCTION:
                return new NativePointer(value);
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("Unsupported type: " + type.getKind());
        }
    }
}
