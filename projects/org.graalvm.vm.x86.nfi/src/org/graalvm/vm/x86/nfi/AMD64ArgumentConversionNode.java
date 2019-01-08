package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.x86.nfi.TypeConversion.AsF32Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsF64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsI64Node;
import org.graalvm.vm.x86.nfi.TypeConversion.AsPointerNode;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF32NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsF64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsI64NodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsPointerNodeGen;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;

import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeSimpleType;
import com.oracle.truffle.nfi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.types.NativeTypeMirror.Kind;

public class AMD64ArgumentConversionNode extends Node {
    @Child private AsPointerNode asPointer = AsPointerNodeGen.create();
    @Child private AsI64Node asI64 = AsI64NodeGen.create();
    @Child private AsF32Node asF32 = AsF32NodeGen.create();
    @Child private AsF64Node asF64 = AsF64NodeGen.create();
    @Child private AsStringNode asString = AsStringNodeGen.create(true);

    // TODO: use proper conversion messages and cache the type
    public ConversionResult execute(NativeTypeMirror type, PosixPointer ptr, Object arg, List<Object> objects, PosixPointer callbacksptr, long envptr) {
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
                            PosixPointer out = Util.strcpy(ptr, str);
                            return new ConversionResult(ptr.getAddress(), out);
                        }
                    }
                    case OBJECT:
                        return new ConversionResult(MagicValues.encodeObject(Util.add(objects, arg)), ptr);
                    case FLOAT:
                        return new ConversionResult(asF32.execute(arg), ptr);
                    case DOUBLE:
                        return new ConversionResult(asF64.execute(arg), ptr);
                    default:
                        CompilerDirectives.transferToInterpreter();
                        throw new AssertionError("unsupported type: " + mirror.getSimpleType());
                }
            }
            case FUNCTION: {
                NativeSignature signature = ((NativeFunctionTypeMirror) type).getSignature();
                Callback cb = new Callback(signature, (TruffleObject) arg);
                boolean fret = false;
                if (signature.getRetType().getKind() == Kind.SIMPLE) {
                    NativeSimpleTypeMirror rettype = (NativeSimpleTypeMirror) signature.getRetType();
                    fret = rettype.getSimpleType() == NativeSimpleType.FLOAT || rettype.getSimpleType() == NativeSimpleType.DOUBLE;
                }
                int id = Util.add(objects, cb);
                long callback = CallbackCode.getCallbackAddress(callbacksptr.getAddress(), id);
                PosixPointer callbackdata = CallbackCode.getCallbackDataPointer(callbacksptr, id);
                callbackdata.setI16((short) id);
                boolean isFloat = fret || Util.isFloatCallback(signature);
                callbackdata.add(2).setI8((byte) (isFloat ? 1 : 0));
                return new ConversionResult(callback, ptr);
            }
            case ENV: {
                return new ConversionResult(envptr, ptr);
            }
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("unsupported type: " + type.getKind());
        }
    }
}
