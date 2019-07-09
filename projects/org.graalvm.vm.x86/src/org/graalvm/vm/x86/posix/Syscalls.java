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

public class Syscalls {
    public static final int SYS_read = 0;
    public static final int SYS_write = 1;
    public static final int SYS_open = 2;
    public static final int SYS_close = 3;
    public static final int SYS_stat = 4;
    public static final int SYS_fstat = 5;
    public static final int SYS_lstat = 6;
    public static final int SYS_poll = 7;
    public static final int SYS_lseek = 8;
    public static final int SYS_mmap = 9;
    public static final int SYS_mprotect = 10;
    public static final int SYS_munmap = 11;
    public static final int SYS_brk = 12;
    public static final int SYS_rt_sigaction = 13;
    public static final int SYS_rt_sigprocmask = 14;
    public static final int SYS_ioctl = 16;
    public static final int SYS_pread64 = 17;
    public static final int SYS_pwrite64 = 18;
    public static final int SYS_readv = 19;
    public static final int SYS_writev = 20;
    public static final int SYS_access = 21;
    public static final int SYS_sched_yield = 24;
    public static final int SYS_dup = 32;
    public static final int SYS_dup2 = 33;
    public static final int SYS_nanosleep = 35;
    public static final int SYS_getpid = 39;
    public static final int SYS_socket = 41;
    public static final int SYS_connect = 42;
    public static final int SYS_sendto = 44;
    public static final int SYS_recvfrom = 45;
    public static final int SYS_recvmsg = 47;
    public static final int SYS_shutdown = 48;
    public static final int SYS_bind = 49;
    public static final int SYS_listen = 50;
    public static final int SYS_getsockname = 51;
    public static final int SYS_getpeername = 52;
    public static final int SYS_setsockopt = 54;
    public static final int SYS_clone = 56;
    public static final int SYS_exit = 60;
    public static final int SYS_uname = 63;
    public static final int SYS_fcntl = 72;
    public static final int SYS_fsync = 74;
    public static final int SYS_getdents = 78;
    public static final int SYS_getcwd = 79;
    public static final int SYS_chdir = 80;
    public static final int SYS_fchdir = 81;
    public static final int SYS_creat = 85;
    public static final int SYS_unlink = 87;
    public static final int SYS_readlink = 89;
    public static final int SYS_gettimeofday = 96;
    public static final int SYS_sysinfo = 99;
    public static final int SYS_times = 100;
    public static final int SYS_getuid = 102;
    public static final int SYS_getgid = 104;
    public static final int SYS_setuid = 105;
    public static final int SYS_setgid = 106;
    public static final int SYS_geteuid = 107;
    public static final int SYS_getegid = 108;
    public static final int SYS_sigaltstack = 131;
    public static final int SYS_arch_prctl = 158;
    public static final int SYS_gettid = 186;
    public static final int SYS_time = 201;
    public static final int SYS_futex = 202;
    public static final int SYS_getdents64 = 217;
    public static final int SYS_set_tid_address = 218;
    public static final int SYS_timer_create = 222;
    public static final int SYS_timer_settime = 223;
    public static final int SYS_timer_delete = 226;
    public static final int SYS_clock_gettime = 228;
    public static final int SYS_clock_getres = 229;
    public static final int SYS_exit_group = 231;
    public static final int SYS_tgkill = 234;
    public static final int SYS_openat = 257;
    public static final int SYS_set_robust_list = 273;
    public static final int SYS_dup3 = 292;
    public static final int SYS_prlimit64 = 302;

    public static final int SYS_DEBUG = 0xDEADBEEF;
    public static final int SYS_PRINTK = 0xDEADBABE;

    public static final int SYS_interop_init = 0xC0DE0000;
    public static final int SYS_interop_error = 0xC0DE0001;
    public static final int SYS_interop_return = 0xC0DE0002;
}
