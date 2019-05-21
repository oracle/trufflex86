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

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.InteropFunctionPointers;
import org.graalvm.vm.x86.node.AMD64RootNode;
import org.graalvm.vm.x86.posix.InteropErrorException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.nfi.spi.types.NativeLibraryDescriptor;

public class AMD64LibraryNode extends AMD64RootNode {
    @Child private AMD64ContextInitializerNode initializer = new AMD64ContextInitializerNode();

    @Child private InterpreterRootNode root;

    private final String libname;

    public AMD64LibraryNode(TruffleLanguage<AMD64Context> language, FrameDescriptor fd, NativeLibraryDescriptor descriptor) {
        super(language, fd);
        this.libname = descriptor.getFilename();
        ArchitecturalState state = language.getContextReference().get().getState();
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static PosixPointer strcpy(PosixPointer dst, String src) {
        return CString.strcpy(dst, src);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        ContextReference<AMD64Context> ctxref = getContextReference();
        AMD64Context ctx = ctxref.get();
        VirtualMemory mem = ctx.getMemory();

        InteropFunctionPointers ptrs = ctx.getInteropFunctionPointers();
        if (ptrs == null) {
            initializer.execute(frame);
            ptrs = ctx.getInteropFunctionPointers();
        }

        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, ctx.getScratchMemory());

        // load library
        long interoplibname = ctx.getScratchMemory();
        if (libname != null) {
            strcpy(ptr, libname);
        } else {
            interoplibname = 0;
        }

        long handle;
        try {
            handle = root.executeInterop(frame, ctx.getSigaltstack(), ctx.getReturnAddress(), ptrs.loadLibrary, interoplibname, 0, 0, 0, 0, 0);
        } catch (InteropErrorException e) {
            CompilerDirectives.transferToInterpreter();
            throw new UnsatisfiedLinkError(e.getMessage());
        }

        return new AMD64Library(ctxref, ptrs.loadLibrary, ptrs.releaseLibrary, ptrs.getSymbol, handle, libname);
    }
}
