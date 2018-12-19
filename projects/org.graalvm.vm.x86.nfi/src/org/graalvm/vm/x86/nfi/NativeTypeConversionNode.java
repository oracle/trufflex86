package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.memory.util.HexFormatter;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class NativeTypeConversionNode extends Node {
    @TruffleBoundary
    private static Object get(List<Object> objects, int id) {
        return objects.get(id);
    }

    public Object execute(NativeTypeMirror type, long value, List<Object> objects) {
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
                            if (!MagicValues.isObject(value)) {
                                CompilerDirectives.transferToInterpreter();
                                throw new AssertionError("Unsupported type: " + mirror.getSimpleType() + " (" + String.format("0x%x", value) + ")");
                            } else {
                                return get(objects, (int) value);
                            }
                        }
                    case VOID:
                        return new NativePointer(0);
                    case FLOAT:
                        return Float.intBitsToFloat((int) value);
                    case DOUBLE:
                        return Double.longBitsToDouble(value);
                    default:
                        CompilerDirectives.transferToInterpreter();
                        throw new AssertionError("Unsupported type: " + mirror.getSimpleType());
                }
            }
            case FUNCTION: {
                NativeFunctionTypeMirror func = (NativeFunctionTypeMirror) type;
                NativeSignature signature = func.getSignature();
                return new AMD64Function("sub_" + HexFormatter.tohex(value, 8), value, signature);
            }
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("Unsupported type: " + type.getKind());
        }
    }
}
