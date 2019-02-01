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

import org.graalvm.vm.memory.MemoryOptions;
import org.graalvm.vm.memory.VirtualMemory;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = NativePointer.class)
public class NativePointerMessageResolution {
    protected static final boolean MEM_MAP_NATIVE = MemoryOptions.MEM_MAP_NATIVE.get();

    @Resolve(message = "IS_POINTER")
    abstract static class IsNativePointerNode extends Node {
        public boolean access(@SuppressWarnings("unused") NativePointer receiver) {
            return true;
        }
    }

    @Resolve(message = "AS_POINTER")
    abstract static class AsNativePointerNode extends Node {
        public long access(NativePointer receiver) {
            if (MEM_MAP_NATIVE) {
                if (receiver.value < 0) {
                    return VirtualMemory.fromMappedNative(receiver.value);
                } else {
                    VirtualMemory mem = AMD64NFILanguage.getCurrentContextReference().get().getMemory();
                    return mem.getNativeAddress(receiver.value);
                }
            }
            return receiver.value;
        }
    }

    @Resolve(message = "TO_NATIVE")
    abstract static class ToNativePointerNode extends Node {
        public NativePointer access(NativePointer receiver) {
            return receiver;
        }
    }

    @Resolve(message = "UNBOX")
    abstract static class UnboxNativePointerNode extends Node {
        public long access(NativePointer receiver) {
            return receiver.value;
        }
    }

    @Resolve(message = "IS_BOXED")
    abstract static class IsBoxedNativePointerNode extends Node {
        @SuppressWarnings("unused")
        public boolean access(NativePointer receiver) {
            return true;
        }
    }

    @Resolve(message = "IS_NULL")
    abstract static class IsNullNativePointerNode extends Node {
        public boolean access(NativePointer receiver) {
            return receiver.value == 0;
        }
    }

    @CanResolve
    abstract static class CanResolveNativePointerNode extends Node {
        public boolean test(TruffleObject receiver) {
            return receiver instanceof NativePointer;
        }
    }
}
