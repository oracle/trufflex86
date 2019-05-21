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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.nfi.spi.types.NativeFunctionTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeSignature;
import com.oracle.truffle.nfi.spi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror;

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
