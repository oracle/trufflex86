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
package org.graalvm.vm.x86.posix;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ArchPrctl extends AMD64Node {
    public static final int ARCH_SET_GS = 0x1001;
    public static final int ARCH_SET_FS = 0x1002;
    public static final int ARCH_GET_FS = 0x1003;
    public static final int ARCH_GET_GS = 0x1004;

    public static final int ARCH_CET_STATUS = 0x3001;
    public static final int ARCH_CET_DISABLE = 0x3002;
    public static final int ARCH_CET_LOCK = 0x3003;
    public static final int ARCH_CET_ALLOC_SHSTK = 0x3004;
    public static final int ARCH_CET_LEGACY_BITMAP = 0x3005;

    @Child private RegisterReadNode readFS;
    @Child private RegisterReadNode readGS;
    @Child private RegisterWriteNode writeFS;
    @Child private RegisterWriteNode writeGS;
    @Child private MemoryWriteNode writeMemory;

    public long execute(VirtualFrame frame, int code, long value) throws SyscallException {
        switch (code) {
            case ARCH_SET_GS:
                if (writeGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeGS = state.getRegisters().getGS().createWrite();
                }
                writeGS.executeI64(frame, value);
                return 0;
            case ARCH_SET_FS:
                if (writeGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeFS = state.getRegisters().getFS().createWrite();
                }
                writeFS.executeI64(frame, value);
                return 0;
            case ARCH_GET_FS:
                if (readFS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    readFS = state.getRegisters().getFS().createRead();
                }
                if (writeMemory == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeMemory = state.createMemoryWrite();
                }
                writeMemory.executeI64(value, readFS.executeI64(frame));
                return 0;
            case ARCH_GET_GS:
                if (readGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    readGS = state.getRegisters().getGS().createRead();
                }
                if (writeMemory == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeMemory = state.createMemoryWrite();
                }
                writeMemory.executeI64(value, readGS.executeI64(frame));
                return 0;
            case ARCH_CET_STATUS:
            case ARCH_CET_DISABLE:
            case ARCH_CET_LOCK:
            case ARCH_CET_ALLOC_SHSTK:
            case ARCH_CET_LEGACY_BITMAP:
                // Intel CET is not (yet?) supported
                throw new SyscallException(Errno.EINVAL);
            default:
                CompilerDirectives.transferToInterpreter();
                System.out.printf("arch_prctl(0x%x): invalid code\n", code);
        }
        throw new SyscallException(Errno.EINVAL);
    }
}
