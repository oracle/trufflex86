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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.ProcessExitException;
import org.graalvm.vm.posix.api.linux.Sched;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class SyscallWrapper extends AMD64Node {
    private static final Logger log = Trace.create(SyscallWrapper.class);

    private final PosixEnvironment posix;
    private final VirtualMemory memory;

    @Child private ArchPrctl prctl;
    @Child private Clone clone;

    public SyscallWrapper(PosixEnvironment posix, VirtualMemory memory) {
        this.posix = posix;
        this.memory = memory;
    }

    private long brk(long addr) {
        if (addr == 0) {
            long brk = memory.brk();
            if (posix.isStrace()) {
                log.log(Level.INFO, () -> String.format("brk(NULL) = 0x%016x", brk));
            }
            if (posix.getTraceWriter() != null) {
                posix.getTraceWriter().brk(addr, brk);
            }
            return brk;
        } else {
            long newbrk = addr;
            long brk = memory.brk(newbrk);
            if (posix.isStrace()) {
                log.log(Level.INFO, () -> String.format("brk(0x%016x) = 0x%016x", newbrk, brk));
            }
            if (posix.getTraceWriter() != null) {
                posix.getTraceWriter().brk(addr, brk);
            }
            return brk;
        }
    }

    @TruffleBoundary
    private static void tracePrctl(int code, long addr) {
        String name;
        switch (code) {
            case ArchPrctl.ARCH_GET_FS:
                name = "ARCH_GET_FS";
                break;
            case ArchPrctl.ARCH_GET_GS:
                name = "ARCH_GET_GS";
                break;
            case ArchPrctl.ARCH_SET_FS:
                name = "ARCH_SET_FS";
                break;
            case ArchPrctl.ARCH_SET_GS:
                name = "ARCH_SET_GS";
                break;
            case ArchPrctl.ARCH_CET_STATUS:
                name = "ARCH_CET_STATUS";
                break;
            case ArchPrctl.ARCH_CET_DISABLE:
                name = "ARCH_CET_DISABLE";
                break;
            case ArchPrctl.ARCH_CET_LOCK:
                name = "ARCH_CET_LOCK";
                break;
            case ArchPrctl.ARCH_CET_ALLOC_SHSTK:
                name = "ARCH_CET_ALLOC_SHSTK";
                break;
            case ArchPrctl.ARCH_CET_LEGACY_BITMAP:
                name = "ARCH_CET_LEGACY_BITMAP";
                break;
            default:
                name = Integer.toString(code);
        }
        log.log(Level.INFO, () -> String.format("arch_prctl(%s, 0x%x)", name, addr));
    }

    @TruffleBoundary
    private static void tracePrctlFail(int errno) {
        log.log(Level.INFO, "arch_prctl failed: " + Errno.toString(errno));
    }

    @TruffleBoundary
    private static void traceClone(long flags, long child_stack, long ptid, long ctid, long newtls) {
        log.log(Level.INFO, () -> String.format("clone(%s, 0x%x, 0x%x, 0x%x, 0x%x)", Sched.clone((int) flags), child_stack, ptid, newtls, ctid));
    }

    @TruffleBoundary
    private static void traceCloneFail(int errno) {
        log.log(Level.INFO, "clone failed: " + Errno.toString(errno));
    }

    public long executeI64(VirtualFrame frame, int nr, long a1, long a2, long a3, long a4, long a5, long a6, long pc) throws SyscallException {
        switch (nr) {
            case Syscalls.SYS_arch_prctl:
                if (prctl == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    prctl = insert(new ArchPrctl());
                }
                if (posix.isStrace()) {
                    tracePrctl((int) a1, a2);
                }
                try {
                    return prctl.execute(frame, (int) a1, a2);
                } catch (SyscallException e) {
                    if (posix.isStrace()) {
                        tracePrctlFail((int) e.getValue());
                    }
                    throw e;
                }
            case Syscalls.SYS_clone:
                if (clone == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    clone = insert(new Clone());
                }
                if (posix.isStrace()) {
                    traceClone(a1, a2, a3, a4, a5);
                }
                try {
                    return clone.execute(frame, a1, a2, a3, a4, a5, pc);
                } catch (SyscallException e) {
                    if (posix.isStrace()) {
                        traceCloneFail((int) e.getValue());
                    }
                    throw e;
                }
        }
        return executeWrapper(nr, a1, a2, a3, a4, a5, a6);
    }

    @TruffleBoundary
    private long executeWrapper(int nr, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException {
        log.log(Levels.DEBUG, () -> String.format("syscall %d: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", nr, a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6));
        switch (nr) {
            case Syscalls.SYS_read:
                return posix.read((int) a1, a2, a3);
            case Syscalls.SYS_write:
                return posix.write((int) a1, a2, a3);
            case Syscalls.SYS_open:
                return posix.open(a1, (int) a2, (int) a3);
            case Syscalls.SYS_close:
                return posix.close((int) a1);
            case Syscalls.SYS_stat:
                return posix.stat(a1, a2);
            case Syscalls.SYS_fstat:
                return posix.fstat((int) a1, a2);
            case Syscalls.SYS_lstat:
                return posix.lstat(a1, a2);
            case Syscalls.SYS_poll:
                return posix.poll(a1, (int) a2, (int) a3);
            case Syscalls.SYS_lseek:
                return posix.lseek((int) a1, a2, (int) a3);
            case Syscalls.SYS_mmap:
                return posix.mmap(a1, a2, (int) a3, (int) a4, (int) a5, a6);
            case Syscalls.SYS_mprotect:
                return posix.mprotect(a1, a2, (int) a3);
            case Syscalls.SYS_munmap:
                return posix.munmap(a1, a2);
            case Syscalls.SYS_brk:
                return brk(a1);
            case Syscalls.SYS_rt_sigaction:
                return posix.rt_sigaction((int) a1, a2, a3);
            case Syscalls.SYS_rt_sigprocmask:
                return posix.rt_sigprocmask((int) a1, a2, a3, (int) a4);
            case Syscalls.SYS_ioctl:
                return posix.ioctl((int) a1, a2, a3);
            case Syscalls.SYS_pread64:
                return posix.pread64((int) a1, a2, (int) a3, a4);
            case Syscalls.SYS_pwrite64:
                return posix.pwrite64((int) a1, a2, (int) a3, a4);
            case Syscalls.SYS_readv:
                return posix.readv((int) a1, a2, (int) a3);
            case Syscalls.SYS_writev:
                return posix.writev((int) a1, a2, (int) a3);
            case Syscalls.SYS_access:
                return posix.access(a1, (int) a2);
            case Syscalls.SYS_dup:
                return posix.dup((int) a1);
            case Syscalls.SYS_dup2:
                return posix.dup2((int) a1, (int) a2);
            case Syscalls.SYS_nanosleep:
                return posix.nanosleep(a1, a2);
            case Syscalls.SYS_getpid:
                return posix.getpid();
            case Syscalls.SYS_socket:
                return posix.socket((int) a1, (int) a2, (int) a3);
            case Syscalls.SYS_connect:
                return posix.connect((int) a1, a2, (int) a3);
            case Syscalls.SYS_sendto:
                return posix.sendto((int) a1, a2, a3, (int) a4, a5, (int) a6);
            case Syscalls.SYS_recvfrom:
                return posix.recvfrom((int) a1, a2, a3, (int) a4, a5, a6);
            case Syscalls.SYS_recvmsg:
                return posix.recvmsg((int) a1, a2, (int) a3);
            case Syscalls.SYS_shutdown:
                return posix.shutdown((int) a1, (int) a2);
            case Syscalls.SYS_bind:
                return posix.bind((int) a1, a2, (int) a3);
            case Syscalls.SYS_listen:
                return posix.listen((int) a1, (int) a2);
            case Syscalls.SYS_getsockname:
                return posix.getsockname((int) a1, a2, a3);
            case Syscalls.SYS_getpeername:
                return posix.getpeername((int) a1, a2, a3);
            case Syscalls.SYS_setsockopt:
                return posix.setsockopt((int) a1, (int) a2, (int) a3, a4, (int) a5);
            case Syscalls.SYS_exit:
                posix.exit((int) a1);
                throw new AssertionError("exit must not return");
            case Syscalls.SYS_uname:
                return posix.uname(a1);
            case Syscalls.SYS_fcntl:
                return posix.fcntl((int) a1, (int) a2, a3);
            case Syscalls.SYS_fsync:
                return posix.fsync((int) a1);
            case Syscalls.SYS_getdents:
                return posix.getdents((int) a1, a2, (int) a3);
            case Syscalls.SYS_getcwd:
                return posix.getcwd(a1, a2);
            case Syscalls.SYS_chdir:
                return posix.chdir(a1);
            case Syscalls.SYS_fchdir:
                return posix.fchdir((int) a1);
            case Syscalls.SYS_creat:
                return posix.creat(a1, (int) a2);
            case Syscalls.SYS_unlink:
                return posix.unlink(a1);
            case Syscalls.SYS_readlink:
                return posix.readlink(a1, a2, a3);
            case Syscalls.SYS_gettimeofday:
                return posix.gettimeofday(a1, a2);
            case Syscalls.SYS_sysinfo:
                return posix.sysinfo(a1);
            case Syscalls.SYS_times:
                return posix.times(a1);
            case Syscalls.SYS_getuid:
                return posix.getuid();
            case Syscalls.SYS_getgid:
                return posix.getgid();
            case Syscalls.SYS_setuid:
                return posix.setuid(a1);
            case Syscalls.SYS_setgid:
                return posix.setgid(a1);
            case Syscalls.SYS_geteuid:
                return posix.geteuid();
            case Syscalls.SYS_getegid:
                return posix.getegid();
            case Syscalls.SYS_sigaltstack:
                return posix.sigaltstack(a1, a2);
            case Syscalls.SYS_gettid:
                return posix.gettid();
            case Syscalls.SYS_time:
                return posix.time(a1);
            case Syscalls.SYS_futex:
                return posix.futex(a1, (int) a2, (int) a3, a4, a5, (int) a6);
            case Syscalls.SYS_getdents64:
                return posix.getdents64((int) a1, a2, (int) a3);
            case Syscalls.SYS_set_tid_address:
                return posix.set_tid_address(a1);
            case Syscalls.SYS_clock_gettime:
                return posix.clock_gettime((int) a1, a2);
            case Syscalls.SYS_clock_getres:
                return posix.clock_getres((int) a1, a2);
            case Syscalls.SYS_exit_group:
                posix.exit_group((int) a1);
                throw new AssertionError("exit must not return");
            case Syscalls.SYS_tgkill:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("tgkill(%d, %d, %d)", (int) a1, (int) a2, (int) a3));
                }
                throw new ProcessExitException(128 + (int) a3);
            case Syscalls.SYS_openat:
                return posix.openat((int) a1, a2, (int) a3, (int) a4);
            case Syscalls.SYS_set_robust_list:
                return posix.set_robust_list(a1, a2);
            case Syscalls.SYS_dup3:
                return posix.dup3((int) a1, (int) a2, (int) a3);
            case Syscalls.SYS_prlimit64:
                return posix.prlimit64((int) a1, (int) a2, a3, a4);
            case Syscalls.SYS_DEBUG:
                log.log(Levels.INFO, String.format("DEBUG: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6));
                return 0;
            case Syscalls.SYS_PRINTK:
                if (posix.isStrace()) {
                    log.log(Level.INFO, "printk(...)");
                }
                posix.printk(a1, a2, a3, a4, a5, a6);
                return 0;
            case Syscalls.SYS_interop_init:
                throw new InteropInitException(a1, a2, a3, a4);
            case Syscalls.SYS_interop_return:
                throw new InteropReturnException(a1);
            case Syscalls.SYS_interop_error:
                throw new InteropErrorException(CString.cstr(new PosixVirtualMemoryPointer(memory, a1)));
            default:
                throw new SyscallException(Errno.ENOSYS);
        }
    }
}
