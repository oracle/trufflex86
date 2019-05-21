/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.nfi;

import java.util.List;

import org.graalvm.vm.posix.api.PosixPointer;
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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.spi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeSignature;
import com.oracle.truffle.nfi.spi.types.NativeSimpleType;
import com.oracle.truffle.nfi.spi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror.Kind;

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
                        return new ConversionResult(asPointer.execute(arg).value, ptr);
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
