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

import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.x86.AMD64;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.InteropFunctionPointers;
import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AMD64ContextInitializerNode extends AMD64Node {
    @Child private InterpreterStartNode interpreter = new InterpreterStartNode();

    public void execute(VirtualFrame frame) {
        ContextReference<AMD64Context> ctxref = getContextReference();
        AMD64Context ctx = ctxref.get();
        VirtualMemory mem = ctx.getMemory();

        InteropFunctionPointers ptrs = ctx.getInteropFunctionPointers();
        if (ptrs == null) {
            ptrs = interpreter.execute(frame);
            ctx.setInteropFunctionPointers(ptrs);

            long len = mem.roundToPageSize(AMD64.SCRATCH_SIZE);
            MemoryPage scratch = mem.allocate(len, "[scratch]");
            try {
                mem.mprotect(scratch.base, scratch.size, true, true, false);
            } catch (PosixException e) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException(e);
            }
            ctx.setScratchMemory(scratch.base);

            MemoryPage callbacks = mem.allocate(4096, "[callbacks]");
            for (int i = 0; i < 128; i++) {
                CallbackCode.writeCallback(mem, callbacks.base, i);
            }
            try {
                mem.mprotect(callbacks.base, callbacks.size, true, true, true);
            } catch (PosixException e) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException(e);
            }
            ctx.setCallbackMemory(callbacks.base);
        }
    }
}
