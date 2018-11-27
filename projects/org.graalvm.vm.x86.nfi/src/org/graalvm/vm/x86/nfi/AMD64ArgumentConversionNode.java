package org.graalvm.vm.x86.nfi;

import java.util.Arrays;
import java.util.List;

import org.graalvm.vm.x86.nfi.TypeConversion.AsI64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsI64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class AMD64ArgumentConversionNode extends Node {
    @Child private AsPointerNode asPointer = AsPointerNodeGen.create();
    @Child private AsI64Node asI64 = AsI64NodeGen.create();
    @Child private AsStringNode asString = AsStringNodeGen.create(true);

    // @formatter:off
    private static final byte[] CALLBACK_TEMPLATE = {
            /* 0 */   (byte) 0xb8, 0x01, 0x02, (byte) 0xef, (byte) 0xbe, // mov    eax,0xbeef0201
            /* 5 */   0x49, (byte) 0x89, (byte) 0xca,                    // mov    r10,rcx
            /* 8 */   0x0f, 0x05,                                        // syscall
            /* a */   (byte) 0xc3                                        // ret
    };
    // @formatter:on

    @TruffleBoundary
    private static PosixPointer createCallback(PosixPointer dst, int id) {
        byte[] code = Arrays.copyOf(CALLBACK_TEMPLATE, CALLBACK_TEMPLATE.length);
        code[0x01] = (byte) id;
        code[0x02] = (byte) (id >> 8);
        return CString.memcpy(dst, code, code.length);
    }

    @TruffleBoundary
    private static PosixPointer strcpy(PosixPointer dst, String src) {
        return CString.strcpy(dst, src);
    }

    @TruffleBoundary
    private static int add(List<Object> objects, Object object) {
        int id = objects.size();
        objects.add(object);
        return id;
    }

    // TODO: use proper conversion messages and cache the type
    public ConversionResult execute(NativeTypeMirror type, PosixPointer ptr, Object arg, List<Object> objects) {
        switch (type.getKind()) {
            case SIMPLE: {
                NativeSimpleTypeMirror mirror = (NativeSimpleTypeMirror) type;
                switch (mirror.getSimpleType()) {
                    case SINT8:
                    case UINT8:
                    case SINT16:
                    case UINT16:
                    case SINT32:
                    case UINT32:
                    case SINT64:
                    case UINT64:
                        return new ConversionResult(asI64.execute(arg), ptr);
                    case POINTER:
                        return new ConversionResult(asPointer.execute((TruffleObject) arg).value, ptr);
                    case STRING: {
                        String str = asString.execute(arg);
                        if (str == null) {
                            return new ConversionResult(0, ptr);
                        } else {
                            PosixPointer out = strcpy(ptr, str);
                            return new ConversionResult(ptr.getAddress(), out);
                        }
                    }
                    case OBJECT:
                        return new ConversionResult(MagicValues.encodeObject(add(objects, arg)), ptr);
                    default:
                        CompilerDirectives.transferToInterpreter();
                        throw new AssertionError("unsupported type: " + mirror.getSimpleType());
                }
            }
            case FUNCTION: {
                NativeSignature signature = ((NativeFunctionTypeMirror) type).getSignature();
                Callback cb = new Callback(signature, (TruffleObject) arg);
                PosixPointer out = createCallback(ptr, add(objects, cb));
                return new ConversionResult(ptr.getAddress(), out);
            }
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("unsupported type: " + type.getKind());
        }
    }
}
