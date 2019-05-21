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
import java.util.logging.Logger;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.InteropCallback;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.posix.SyscallException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.nfi.spi.types.NativeSignature;
import com.oracle.truffle.nfi.spi.types.NativeSimpleType;
import com.oracle.truffle.nfi.spi.types.NativeSimpleTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror;
import com.oracle.truffle.nfi.spi.types.NativeTypeMirror.Kind;

public class AMD64FunctionCallNode extends AMD64Node {
    private static final Logger log = Trace.create(AMD64FunctionCallNode.class);

    @Child private InterpreterRootNode root;
    @Child private AMD64ArgumentConversionNode converter = new AMD64ArgumentConversionNode();

    @Child private CallbackNode foreignCall = CallbackNodeGen.create();

    public AMD64FunctionCallNode(ArchitecturalState state) {
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static NativeTypeMirror getType(NativeSignature signature, int i) {
        return signature.getArgTypes().get(i);
    }

    public long execute(VirtualFrame frame, AMD64Symbol func, NativeSignature signature, Object[] args, List<Object> objects) {
        AMD64Context ctx = getContextReference().get();

        boolean returnFloat = false;

        if (signature.getRetType().getKind() == NativeTypeMirror.Kind.SIMPLE) {
            NativeSimpleTypeMirror type = (NativeSimpleTypeMirror) signature.getRetType();
            if (type.getSimpleType() == NativeSimpleType.DOUBLE || type.getSimpleType() == NativeSimpleType.FLOAT) {
                returnFloat = true;
            }
        }

        VirtualMemory mem = ctx.getMemory();
        long pname = ctx.getScratchMemory();
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, pname);
        long callbacks = ctx.getCallbackMemory();
        PosixPointer callbackptr = new PosixVirtualMemoryPointer(mem, callbacks);
        long envptr = ctx.getInteropFunctionPointers().truffleEnv;

        InteropCallback cb = new InteropCallback() {
            public long call(int id, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
                return call(id, a1, a2, a3, a4, a5, a6, 0, 0, 0, 0, 0, 0, 0, 0);
            }

            public long call(int id, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5, long f6, long f7, long f8) throws SyscallException {
                CompilerAsserts.neverPartOfCompilation();

                if (id < 0 || id >= objects.size()) {
                    log.warning("Unknown callback: " + id);
                    throw new SyscallException(Errno.ENOSYS);
                }

                if (!(objects.get(id) instanceof Callback)) {
                    log.warning("Unknown callback: " + id);
                    throw new SyscallException(Errno.ENOSYS);
                }

                Callback callback = (Callback) objects.get(id);
                long result = foreignCall.execute(callback.signature, objects, callback.object, a1, a2, a3, a4, a5, a6, f1, f2, f3, f4, f5, f6, f7, f8);
                return result;
            }
        };

        long sp = ctx.getSigaltstack();
        long ret = ctx.getReturnAddress();
        long[] rawargs = new long[6];
        long[] floatargs = new long[8];
        int intidx = 0;
        int floatidx = 0;
        int argidx = 0;
        int stackargcnt = args.length - rawargs.length;
        if (stackargcnt > 0) {
            sp -= stackargcnt * 8;
        }
        for (int i = 0; i < signature.getFixedArgCount(); i++) {
            NativeTypeMirror type = getType(signature, i);
            if (type.getKind() == Kind.ENV) {
                long value = converter.execute(type, ptr, null, null, callbackptr, envptr).value;
                if (intidx >= rawargs.length) {
                    sp += 8;
                    mem.setI64(sp, value);
                } else {
                    rawargs[intidx++] = value;
                }
            } else {
                ConversionResult result = converter.execute(type, ptr, args[argidx++], objects, callbackptr, envptr);
                if (result.isFloat) {
                    floatargs[floatidx++] = result.value;
                } else {
                    if (intidx >= rawargs.length) {
                        sp += 8;
                        mem.setI64(sp, result.value);
                    } else {
                        rawargs[intidx++] = result.value;
                    }
                }
                ptr = result.ptr;
            }
        }
        if (stackargcnt > 0) {
            sp -= stackargcnt * 8;
        }

        long a1 = rawargs[0];
        long a2 = rawargs[1];
        long a3 = rawargs[2];
        long a4 = rawargs[3];
        long a5 = rawargs[4];
        long a6 = rawargs[5];

        long f1 = floatargs[0];
        long f2 = floatargs[1];
        long f3 = floatargs[2];
        long f4 = floatargs[3];
        long f5 = floatargs[4];
        long f6 = floatargs[5];
        long f7 = floatargs[6];
        long f8 = floatargs[7];

        ctx.setInteropCallback(cb);

        try {
            return root.executeInterop(frame, sp, ret, func.getAddress(), a1, a2, a3, a4, a5, a6, f1, f2, f3, f4, f5, f6, f7, f8, returnFloat);
        } finally {
            ctx.clearInteropCallback();
        }
    }
}
