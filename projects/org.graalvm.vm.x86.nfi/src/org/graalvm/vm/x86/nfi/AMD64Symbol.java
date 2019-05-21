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

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.x86.AMD64Context;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.nfi.spi.NativeSymbolLibrary;
import com.oracle.truffle.nfi.spi.types.NativeSignature;

@ExportLibrary(InteropLibrary.class)
@ExportLibrary(NativeSymbolLibrary.class)
public class AMD64Symbol implements TruffleObject {
    protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    private final String name;
    private final long address;

    protected static RootCallTarget createCallTarget(AMD64Context ctx) {
        return Truffle.getRuntime().createCallTarget(new AMD64FunctionCallTarget(ctx.getLanguage(), ctx.getFrameDescriptor()));
    }

    protected static NativeTypeConversionNode createNativeTypeConversionNode() {
        return new NativeTypeConversionNode();
    }

    static Object create(AMD64NFILanguage language, String name, long address) {
        if (address == 0) { // NULL pointers can never be executed
            return new AMD64Symbol(name, address);
        }
        return language.getTools().createBindableSymbol(new AMD64Symbol(name, address));
    }

    static Object createBound(AMD64NFILanguage language, String name, long address, NativeSignature signature) {
        if (address == 0) { // NULL pointers can never be executed
            return new AMD64Symbol(name, address);
        }
        return language.getTools().createBoundSymbol(new AMD64Symbol(name, address), signature);
    }

    public AMD64Symbol(String name, long address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public long getAddress() {
        return address;
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        return "AMD64Symbol[" + name + "=0x" + HexFormatter.tohex(address, 1) + "]";
    }

    @ExportMessage
    boolean isPointer() {
        return true;
    }

    @ExportMessage
    long asPointer(@CachedContext(AMD64NFILanguage.class) AMD64Context ctx) {
        if (MEM_MAP_NATIVE) {
            if (address < 0) {
                return VirtualMemory.fromMappedNative(address);
            } else {
                VirtualMemory mem = ctx.getMemory();
                return mem.getNativeAddress(address);
            }
        } else {
            return address;
        }
    }

    @ExportMessage
    boolean isNull() {
        return address == 0;
    }

    @ExportMessage
    boolean isBindable() {
        return address != 0;
    }

    @ExportMessage
    Object prepareSignature(NativeSignature signature) {
        return signature;
    }

    @SuppressWarnings("unused")
    @ExportMessage
    Object call(Object signature, Object[] args,
                    @CachedContext(AMD64NFILanguage.class) AMD64Context ctx,
                    @Cached(value = "createCallTarget(ctx)", allowUncached = true) CallTarget execute,
                    @Cached(value = "createNativeTypeConversionNode()", allowUncached = true) NativeTypeConversionNode converter,
                    @CachedLanguage AMD64NFILanguage language) throws ArityException, UnsupportedTypeException {
        if (!(signature instanceof NativeSignature)) {
            CompilerDirectives.transferToInterpreter();
            throw UnsupportedTypeException.create(new Object[]{signature});
        }

        List<Object> objects = new ArrayList<>();
        long result = (long) execute.call(new Object[]{this, signature, args, objects});
        return converter.execute(((NativeSignature) signature).getRetType(), result, objects, language);
    }
}
