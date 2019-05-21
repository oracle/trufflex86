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

import org.graalvm.vm.util.HexFormatter;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.nfi.spi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeSignature;
import com.oracle.truffle.nfi.spi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror;

public class NativeTypeConversionNode extends Node {
    @TruffleBoundary
    private static Object get(List<Object> objects, int id) {
        return objects.get(id);
    }

    @TruffleBoundary
    private static String getSubName(long addr) {
        return "sub_" + HexFormatter.tohex(addr, 1);
    }

    public Object execute(NativeTypeMirror type, long value, List<Object> objects, @CachedLanguage AMD64NFILanguage language) {
        switch (type.getKind()) {
            case SIMPLE: {
                NativeSimpleTypeMirror mirror = (NativeSimpleTypeMirror) type;
                switch (mirror.getSimpleType()) {
                    case SINT8:
                        return (byte) value;
                    case UINT8:
                        return value & 0xFF;
                    case SINT16:
                        return (short) value;
                    case UINT16:
                        return value & 0xFFFF;
                    case SINT32:
                        return (int) value;
                    case UINT32:
                        return value & 0xFFFFFFFFL;
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
                return AMD64Symbol.createBound(language, getSubName(value), value, signature);
            }
            default:
                CompilerDirectives.transferToInterpreter();
                throw new AssertionError("Unsupported type: " + type.getKind());
        }
    }
}
