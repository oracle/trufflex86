package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;

import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class AMD64ArgumentConversionNode extends Node {
    @Child private AsPointerNode asPointer = AsPointerNodeGen.create();

    // TODO: use proper conversion messages and cache the type
    public ConversionResult execute(NativeTypeMirror type, PosixPointer ptr, Object arg) {
        switch (type.getKind()) {
            case SIMPLE: {
                NativeSimpleTypeMirror mirror = (NativeSimpleTypeMirror) type;
                switch (mirror.getSimpleType()) {
                    case SINT8:
                    case UINT8:
                        return new ConversionResult((byte) arg, ptr);
                    case SINT16:
                    case UINT16:
                        return new ConversionResult((short) arg, ptr);
                    case SINT32:
                    case UINT32:
                        return new ConversionResult((int) arg, ptr);
                    case SINT64:
                    case UINT64:
                        return new ConversionResult((long) arg, ptr);
                    case POINTER:
                        return new ConversionResult(asPointer.execute((TruffleObject) arg).value, ptr);
                    default:
                        CompilerDirectives.transferToInterpreter();
                        throw new AssertionError("unsupported type: " + mirror.getSimpleType());
                }
            }
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("unsupported type: " + type.getKind());
        }
    }
}
