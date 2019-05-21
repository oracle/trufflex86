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

import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.PosixPointer;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.nfi.spi.types.NativeSignature;
import com.oracle.truffle.nfi.spi.types.NativeSimpleType;
import com.oracle.truffle.nfi.spi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror.Kind;

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
