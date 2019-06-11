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
