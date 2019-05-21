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

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.nfi.TypeConversion.AsStringNode;
import org.graalvm.vm.x86.nfi.TypeConversionFactory.AsStringNodeGen;
import org.graalvm.vm.x86.posix.InteropErrorException;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node.Child;

@ExportLibrary(InteropLibrary.class)
public class AMD64Library implements TruffleObject {
    private static final EmptyKeysArray KEYS = new EmptyKeysArray();

    private final long loadLibrary;
    private final long releaseLibrary;
    private final long getSymbol;

    private final long handle;

    private final ContextReference<AMD64Context> ctxref;

    private final String name;

    public AMD64Library(ContextReference<AMD64Context> ctxref, long loadLibrary, long releaseLibrary, long getSymbol, long handle, String name) {
        this.ctxref = ctxref;
        this.loadLibrary = loadLibrary;
        this.releaseLibrary = releaseLibrary;
        this.getSymbol = getSymbol;
        this.handle = handle;
        this.name = name;
    }

    public long getLoadLibrary() {
        return loadLibrary;
    }

    public long getReleaseLibrary() {
        return releaseLibrary;
    }

    public long getSymbol() {
        return getSymbol;
    }

    public long getHandle() {
        return handle;
    }

    public ContextReference<AMD64Context> getContextReference() {
        return ctxref;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AMD64Library[" + name + "]";
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return KEYS;
    }

    @ExportMessage
    @ImportStatic(AMD64NFILanguage.class)
    abstract static class IsMemberReadable {
        @Child protected static AsStringNode asString = AsStringNodeGen.create(true);

        protected static RootCallTarget createSymbolLookupNode(AMD64Context ctx) {
            AMD64SymbolLookupCallTarget root = new AMD64SymbolLookupCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
            return Truffle.getRuntime().createCallTarget(root);
        }

        @Specialization
        static boolean doGeneric(AMD64Library receiver, String symbol,
                        @SuppressWarnings("unused") @CachedContext(AMD64NFILanguage.class) AMD64Context ctx,
                        @Cached(value = "createSymbolLookupNode(ctx)", allowUncached = true) RootCallTarget lookup) {
            String symname = asString.execute(symbol);
            try {
                lookup.call(new Object[]{receiver, symname});
                return true;
            } catch (InteropErrorException e) {
                return false;
            }
        }
    }

    @ExportMessage
    @ImportStatic(AMD64NFILanguage.class)
    abstract static class ReadMember {
        @Child protected static AsStringNode asString = AsStringNodeGen.create(true);

        protected static RootCallTarget createSymbolLookupNode(AMD64Context ctx) {
            AMD64SymbolLookupCallTarget root = new AMD64SymbolLookupCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor());
            return Truffle.getRuntime().createCallTarget(root);
        }

        @Specialization
        static Object doGeneric(AMD64Library receiver, String symbol,
                        @CachedLanguage AMD64NFILanguage language,
                        @SuppressWarnings("unused") @CachedContext(AMD64NFILanguage.class) AMD64Context ctx,
                        @Cached(value = "createSymbolLookupNode(ctx)", allowUncached = true) RootCallTarget lookup) throws UnknownIdentifierException {
            String symname = asString.execute(symbol);
            try {
                long ptr = (long) lookup.call(new Object[]{receiver, symname});
                return AMD64Symbol.create(language, symname, ptr);
            } catch (InteropErrorException e) {
                throw UnknownIdentifierException.create(symname);
            }
        }
    }

    @SuppressWarnings("static-method")
    @ExportLibrary(InteropLibrary.class)
    static final class EmptyKeysArray implements TruffleObject {

        @ExportMessage
        boolean hasArrayElements() {
            return true;
        }

        @ExportMessage
        long getArraySize() {
            return 0;
        }

        @ExportMessage
        boolean isArrayElementReadable(@SuppressWarnings("unused") long index) {
            return false;
        }

        @ExportMessage
        Object readArrayElement(long index) throws InvalidArrayIndexException {
            throw InvalidArrayIndexException.create(index);
        }
    }
}
