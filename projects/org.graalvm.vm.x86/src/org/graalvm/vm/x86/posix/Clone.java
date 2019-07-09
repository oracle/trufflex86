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
import org.graalvm.vm.posix.api.linux.Sched;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Clone extends AMD64Node {
    @Child private MemoryWriteNode memory;
    @Child private CopyToCpuStateNode read = new CopyToCpuStateNode();
    @CompilationFinal private FrameSlot cpuStateSlot;
    @CompilationFinal private FrameSlot gprMaskSlot;
    @CompilationFinal private FrameSlot avxMaskSlot;

    @CompilationFinal private ContextReference<AMD64Context> ctxref;

    public long execute(VirtualFrame frame, long flags, long child_stack, long ptid, long ctid, long newtls, long pc) throws SyscallException {
        // general error handling
        if (BitTest.test(flags, Sched.CLONE_SIGHAND) && !BitTest.test(flags, Sched.CLONE_VM)) {
            throw new SyscallException(Errno.EINVAL);
        }
        if (BitTest.test(flags, Sched.CLONE_THREAD) && !BitTest.test(flags, Sched.CLONE_SIGHAND)) {
            throw new SyscallException(Errno.EINVAL);
        }
        if (BitTest.test(flags, Sched.CLONE_FS) && BitTest.test(flags, Sched.CLONE_NEWNS)) {
            throw new SyscallException(Errno.EINVAL);
        }
        if (BitTest.test(flags, Sched.CLONE_FS) && BitTest.test(flags, Sched.CLONE_NEWUSER)) {
            throw new SyscallException(Errno.EINVAL);
        }
        if (BitTest.test(flags, Sched.CLONE_NEWIPC) && BitTest.test(flags, Sched.CLONE_SYSVSEM)) {
            throw new SyscallException(Errno.EINVAL);
        }
        if ((BitTest.test(flags, Sched.CLONE_NEWPID) || BitTest.test(flags, Sched.CLONE_NEWUSER)) && (BitTest.test(flags, Sched.CLONE_THREAD) || BitTest.test(flags, Sched.CLONE_PARENT))) {
            throw new SyscallException(Errno.EINVAL);
        }
        if (BitTest.test(flags, Sched.CLONE_NEWCGROUP) || BitTest.test(flags, Sched.CLONE_NEWIPC) || BitTest.test(flags, Sched.CLONE_NEWNET) || BitTest.test(flags, Sched.CLONE_NEWNS) ||
                        BitTest.test(flags, Sched.CLONE_NEWPID) || BitTest.test(flags, Sched.CLONE_NEWUTS)) {
            throw new SyscallException(Errno.EPERM);
        }
        if ((child_stack & 7) != 0) { // stack should be aligned to long
            throw new SyscallException(Errno.EINVAL);
        }

        // vmx86 specific: only threads are supported
        if (!BitTest.test(flags, Sched.CLONE_VM) || !BitTest.test(flags, Sched.CLONE_FS) || !BitTest.test(flags, Sched.CLONE_FILES) || !BitTest.test(flags, Sched.CLONE_SIGHAND) ||
                        !BitTest.test(flags, Sched.CLONE_THREAD) || !BitTest.test(flags, Sched.CLONE_SYSVSEM)) {
            throw new SyscallException(Errno.EINVAL);
        }

        if (cpuStateSlot == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ctxref = getContextReference();
            AMD64Context ctx = ctxref.get();
            cpuStateSlot = ctx.getCpuState();
            gprMaskSlot = ctx.getGPRMask();
            avxMaskSlot = ctx.getAVXMask();
            memory = ctx.getState().createMemoryWrite();
        }

        CpuState state;
        if (gprMaskSlot != null) {
            boolean[] gprMask = (boolean[]) FrameUtil.getObjectSafe(frame, gprMaskSlot);
            boolean[] avxMask = (boolean[]) FrameUtil.getObjectSafe(frame, avxMaskSlot);
            CompilerAsserts.partialEvaluationConstant(gprMask);
            CompilerAsserts.partialEvaluationConstant(avxMask);

            CpuState initialState = (CpuState) FrameUtil.getObjectSafe(frame, cpuStateSlot);
            if (gprMask != null) {
                state = read.execute(frame, pc, initialState.clone(), gprMask, avxMask);
            } else {
                state = read.execute(frame, pc);
            }
        } else {
            state = read.execute(frame, pc);
        }

        state.rip = pc + 2;
        state.rsp = child_stack;

        if (BitTest.test(flags, Sched.CLONE_SETTLS)) {
            state.fs = newtls;
        }

        AMD64Context ctx = ctxref.get();
        CallTarget threadMain = ctx.getInterpreter();
        PosixEnvironment posix = ctx.getPosixEnvironment();
        if (posix.getThreadCount() >= posix.getPosix().getProcessInfo().rlimit_nproc) {
            throw new SyscallException(Errno.EAGAIN);
        }

        int tid = PosixEnvironment.allocateTid();

        if (BitTest.test(flags, Sched.CLONE_PARENT_SETTID)) {
            memory.executeI32(ptid, tid);
        }

        if (BitTest.test(flags, Sched.CLONE_CHILD_SETTID)) {
            memory.executeI32(ctid, tid);
        }

        Thread t = ctx.createThread(tid, () -> {
            PosixEnvironment.setTid(tid);
            if (BitTest.test(flags, Sched.CLONE_CHILD_CLEARTID)) {
                posix.setTidAddress(ctid);
            }

            state.rax = 0;
            threadMain.call(state);
        });
        t.setDaemon(true);
        t.setName("clone@0x" + HexFormatter.tohex(pc, 16));
        t.start();

        return tid;
    }
}
