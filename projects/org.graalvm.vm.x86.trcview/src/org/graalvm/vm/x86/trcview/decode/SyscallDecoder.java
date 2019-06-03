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
package org.graalvm.vm.x86.trcview.decode;

import static org.graalvm.vm.util.HexFormatter.tohex;

import org.graalvm.vm.posix.api.Clock;
import org.graalvm.vm.posix.api.Signal;
import org.graalvm.vm.posix.api.Unistd;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Ioctls;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.linux.Futex;
import org.graalvm.vm.posix.api.linux.Sched;
import org.graalvm.vm.posix.api.mem.Mman;
import org.graalvm.vm.posix.api.net.Socket;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.posix.ArchPrctl;
import org.graalvm.vm.x86.posix.SyscallNames;
import org.graalvm.vm.x86.posix.Syscalls;

public class SyscallDecoder {
    public static final int SEEK_SET = 0;
    public static final int SEEK_CUR = 1;
    public static final int SEEK_END = 2;

    private static String whence(long whence) {
        switch ((int) whence) {
            case SEEK_SET:
                return "SEEK_SET";
            case SEEK_CUR:
                return "SEEK_CUR";
            case SEEK_END:
                return "SEEK_END";
            default:
                return Long.toString(whence);
        }
    }

    private static String archprctl(int code) {
        switch (code) {
            case ArchPrctl.ARCH_GET_FS:
                return "ARCH_GET_FS";
            case ArchPrctl.ARCH_GET_GS:
                return "ARCH_GET_GS";
            case ArchPrctl.ARCH_SET_FS:
                return "ARCH_SET_FS";
            case ArchPrctl.ARCH_SET_GS:
                return "ARCH_SET_GS";
            case ArchPrctl.ARCH_CET_STATUS:
                return "ARCH_CET_STATUS";
            case ArchPrctl.ARCH_CET_DISABLE:
                return "ARCH_CET_DISABLE";
            case ArchPrctl.ARCH_CET_LOCK:
                return "ARCH_CET_LOCK";
            case ArchPrctl.ARCH_CET_ALLOC_SHSTK:
                return "ARCH_CET_ALLOC_SHSTK";
            case ArchPrctl.ARCH_CET_LEGACY_BITMAP:
                return "ARCH_CET_LEGACY_BITMAP";
            default:
                return Integer.toString(code);
        }
    }

    private static String hex(long x) {
        return tohex(x, 1);
    }

    public static String decode(CpuState state) {
        int id = (int) state.rax;
        long a1 = state.rdi;
        long a2 = state.rsi;
        long a3 = state.rdx;
        long a4 = state.r10;
        long a5 = state.r8;
        long a6 = state.r9;
        switch (id) {
            case Syscalls.SYS_read:
                return "read(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_write:
                return "write(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_open:
                return "open(0x" + hex(a1) + ", " + Fcntl.flags((int) a2) + ", " + Stat.mode((int) a3) + ")";
            case Syscalls.SYS_close:
                return "close(" + a1 + ")";
            case Syscalls.SYS_stat:
                return "stat(0x" + hex(a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_fstat:
                return "fstat(" + a1 + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_lstat:
                return "lstat(0x" + hex(a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_poll:
                return "poll(0x" + hex(a1) + ", " + a2 + ", " + a3 + ")";
            case Syscalls.SYS_lseek:
                return "lseek(" + a1 + ", " + a2 + ", " + whence(a3) + ")";
            case Syscalls.SYS_mmap:
                return "mmap(0x" + hex(a1) + ", " + a2 + ", " + Mman.prot((int) a3) + ", " + Mman.flags((int) a4) + ", " + a5 + ", " + a6 + ")";
            case Syscalls.SYS_mprotect:
                return "mprotect(0x" + hex(a1) + ", " + a2 + ", " + Mman.prot((int) a3) + ")";
            case Syscalls.SYS_munmap:
                return "munmap(0x" + hex(a1) + ", " + a2 + ")";
            case Syscalls.SYS_brk:
                return "brk(0x" + hex(a1) + ")";
            case Syscalls.SYS_rt_sigaction:
                return "rt_sigaction(" + Signal.toString((int) a1) + ", 0x" + hex(a2) + ", 0x" + hex(a3) + ")";
            case Syscalls.SYS_rt_sigprocmask:
                return "rt_sigprocmask(" + Signal.sigprocmaskHow((int) a1) + ", 0x" + hex(a2) + ", 0x" + hex(a3) + ", " + a4 + ")";
            case Syscalls.SYS_ioctl:
                return "ioctl(" + a1 + ", " + Ioctls.toString(org.graalvm.vm.x86.posix.Ioctls.translate((int) a2)) + " /* 0x" + tohex(a2, 8) + " */, 0x" + hex(a3) + ")";
            case Syscalls.SYS_pread64:
                return "pread64(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ", " + a4 + ")";
            case Syscalls.SYS_pwrite64:
                return "pwrite64(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ", " + a4 + ")";
            case Syscalls.SYS_readv:
                return "readv(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_writev:
                return "writev(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_access:
                return "access(0x" + hex(a1) + ", " + Unistd.amode((int) a2) + ")";
            case Syscalls.SYS_dup:
                return "dup(" + a1 + ")";
            case Syscalls.SYS_dup2:
                return "dup2(" + a1 + ", " + a2 + ")";
            case Syscalls.SYS_nanosleep:
                return "nanosleep(0x" + hex(a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_getpid:
                return "getpid()";
            case Syscalls.SYS_socket:
                return "socket(" + Socket.addressFamily((int) a1) + ", " + Socket.type((int) a2) + ", " + Socket.protocol((int) a1, (int) a3) + ")";
            case Syscalls.SYS_connect:
                return "connect(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_sendto:
                return "sendto(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ", " + Socket.sendrecvFlags((int) a4) + ", 0x" + hex(a5) + ", " + a6 + ")";
            case Syscalls.SYS_recvfrom:
                return "recvfrom(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ", " + Socket.sendrecvFlags((int) a4) + ", 0x" + hex(a5) + ", 0x" + hex(a6) + ")";
            case Syscalls.SYS_recvmsg:
                return "recvmsg(" + a1 + ", 0x" + hex(a2) + ", " + Socket.sendrecvFlags((int) a3) + ")";
            case Syscalls.SYS_shutdown:
                return "shutdown(" + a1 + ", " + Socket.shutdownHow((int) a2) + ")";
            case Syscalls.SYS_bind:
                return "bind(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_listen:
                return "listen(" + a1 + ", " + a2 + ")";
            case Syscalls.SYS_getsockname:
                return "getsockname(" + a1 + ", 0x" + hex(a2) + ", 0x" + hex(a3) + ")";
            case Syscalls.SYS_getpeername:
                return "getpeername(" + a1 + ", 0x" + hex(a2) + ", 0x" + hex(a3) + ")";
            case Syscalls.SYS_setsockopt:
                return "setsockopt(" + a1 + ", " + Socket.sockoptLevel((int) a2) + ", " + Socket.sockoptOption((int) a2, (int) a3) + ", 0x" + hex(a4) + ", " + a5 + ")";
            case Syscalls.SYS_clone:
                return "clone(" + Sched.clone((int) a1) + ", 0x" + hex(a2) + ", 0x" + hex(a3) + ", 0x" + hex(a4) + ", 0x" + hex(a5) + ")";
            case Syscalls.SYS_exit:
                return "exit(" + a1 + ")";
            case Syscalls.SYS_uname:
                return "uname(0x" + hex(a1) + ")";
            case Syscalls.SYS_fcntl:
                return "fcntl(" + a1 + ", " + Fcntl.fcntl((int) a2) + ", 0x" + hex(a3) + ")";
            case Syscalls.SYS_fsync:
                return "fsync(" + a1 + ")";
            case Syscalls.SYS_getdents:
                return "getdents(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_getcwd:
                return "getcwd(0x" + hex(a1) + ", " + a2 + ")";
            case Syscalls.SYS_creat:
                return "creat(0x" + hex(a1) + ", " + Stat.mode((int) a2) + ")";
            case Syscalls.SYS_unlink:
                return "unlink(0x" + hex(a1) + ")";
            case Syscalls.SYS_readlink:
                return "readlink(0x" + hex(a1) + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_gettimeofday:
                return "gettimeofday(0x" + hex(a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_sysinfo:
                return "sysinfo(0x" + hex(a1) + ")";
            case Syscalls.SYS_times:
                return "times(0x" + hex(a1) + ")";
            case Syscalls.SYS_getuid:
                return "getuid()";
            case Syscalls.SYS_getgid:
                return "getgid()";
            case Syscalls.SYS_setuid:
                return "setuid(" + a1 + ")";
            case Syscalls.SYS_setgid:
                return "setgid(" + a1 + ")";
            case Syscalls.SYS_geteuid:
                return "geteuid()";
            case Syscalls.SYS_getegid:
                return "getegid()";
            case Syscalls.SYS_sigaltstack:
                return "sigaltstack(0x" + hex(a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_arch_prctl:
                return "arch_prctl(" + archprctl((int) a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_gettid:
                return "gettid()";
            case Syscalls.SYS_time:
                return "time(0x" + hex(a1) + ")";
            case Syscalls.SYS_futex:
                return "futex(0x" + hex(a1) + ", " + Futex.op((int) a2) + ", " + a3 + ", 0x" + hex(a4) + ", 0x" + hex(a5) + ", " + a6 + ")";
            case Syscalls.SYS_getdents64:
                return "getdents64(" + a1 + ", 0x" + hex(a2) + ", " + a3 + ")";
            case Syscalls.SYS_set_tid_address:
                return "set_tid_address(0x" + hex(a1) + ")";
            case Syscalls.SYS_clock_gettime:
                return "clock_gettime(" + Clock.getClockName((int) a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_clock_getres:
                return "clock_getres(" + Clock.getClockName((int) a1) + ", 0x" + hex(a2) + ")";
            case Syscalls.SYS_exit_group:
                return "exit_group(" + a1 + ")";
            case Syscalls.SYS_tgkill:
                return "tgkill(" + a1 + ", " + a2 + ", " + Signal.toString((int) a3) + ")";
            case Syscalls.SYS_openat:
                return "open(" + a1 + ", 0x" + hex(a2) + ", " + Fcntl.flags((int) a3) + ", " + Stat.mode((int) a4) + ")";
            case Syscalls.SYS_dup3:
                return "dup3(" + a1 + ", " + a2 + ", " + a3 + ")";
            case Syscalls.SYS_prlimit64:
                return "prlimit64(" + a1 + ", " + a2 + ", 0x" + hex(a3) + ", 0x" + hex(a4) + ")";
            default:
                return SyscallNames.getName(id);
        }
    }
}
