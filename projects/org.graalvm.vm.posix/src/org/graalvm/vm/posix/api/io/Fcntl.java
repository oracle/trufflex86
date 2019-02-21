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
package org.graalvm.vm.posix.api.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.util.BitTest;

public class Fcntl {
    // @formatter:off
    public static final int O_ACCMODE    =  00000003;
    public static final int O_RDONLY     =  00000000;
    public static final int O_WRONLY     =  00000001;
    public static final int O_RDWR       =  00000002;
    public static final int O_CREAT      =  00000100;        /* not fcntl */
    public static final int O_EXCL       =  00000200;        /* not fcntl */
    public static final int O_NOCTTY     =  00000400;        /* not fcntl */
    public static final int O_TRUNC      =  00001000;        /* not fcntl */
    public static final int O_APPEND     =  00002000;
    public static final int O_NONBLOCK   =  00004000;
    public static final int O_DSYNC      =  00010000;        /* used to be O_SYNC, see below */
    public static final int FASYNC       =  00020000;        /* fcntl, for BSD compatibility */
    public static final int O_DIRECT     =  00400000;        /* direct disk access hint */
    public static final int O_LARGEFILE  =  00200000;
    public static final int O_DIRECTORY  =  00040000;        /* must be a directory */
    public static final int O_NOFOLLOW   =  00100000;        /* don't follow links */
    public static final int O_NOATIME    =  01000000;
    public static final int O_CLOEXEC    =  02000000;        /* set close_on_exec */
    public static final int O_TMPFILE    = 020000000;

    public static final int F_DUPFD         = 0;             /* dup */
    public static final int F_GETFD         = 1;             /* get close_on_exec */
    public static final int F_SETFD         = 2;             /* set/clear close_on_exec */
    public static final int F_GETFL         = 3;             /* get file->f_flags */
    public static final int F_SETFL         = 4;             /* set file->f_flags */
    public static final int F_GETLK         = 5;
    public static final int F_SETLK         = 6;
    public static final int F_SETLKW        = 7;
    public static final int F_SETOWN        = 8;             /* for sockets. */
    public static final int F_GETOWN        = 9;             /* for sockets. */
    public static final int F_SETSIG        = 10;            /* for sockets. */
    public static final int F_GETSIG        = 11;            /* for sockets. */
    public static final int F_GETLK64       = 12;            /*  using 'struct flock64' */
    public static final int F_SETLK64       = 13;
    public static final int F_SETLKW64      = 14;

    public static final int F_SETOWN_EX     = 15;
    public static final int F_GETOWN_EX     = 16;

    public static final int F_GETOWNER_UIDS = 17;

    public static final int F_LINUX_SPECIFIC_BASE = 1024;
    public static final int F_DUPFD_CLOEXEC = F_LINUX_SPECIFIC_BASE + 6;

    public static final int AT_FDCWD        = -100;          /* Special value used to indicate
                                                                the *at functions should use the
                                                                current working directory. */

    public static final int FD_CLOEXEC      = 1;             /* actually anything with low bit set goes */
    // @formatter:on

    public static String flags(int flags) {
        List<String> result = new ArrayList<>();
        int rw = flags & O_ACCMODE;
        switch (rw) {
            case O_RDONLY:
                result.add("O_RDONLY");
                break;
            case O_WRONLY:
                result.add("O_WRONLY");
                break;
            case O_RDWR:
                result.add("O_RDWR");
                break;
        }
        if (BitTest.test(flags, O_CREAT)) {
            result.add("O_CREAT");
        }
        if (BitTest.test(flags, O_EXCL)) {
            result.add("O_EXCL");
        }
        if (BitTest.test(flags, O_NOCTTY)) {
            result.add("O_NOCTTY");
        }
        if (BitTest.test(flags, O_TRUNC)) {
            result.add("O_TRUNC");
        }
        if (BitTest.test(flags, O_APPEND)) {
            result.add("O_APPEND");
        }
        if (BitTest.test(flags, O_NONBLOCK)) {
            result.add("O_NONBLOCK");
        }
        if (BitTest.test(flags, O_DSYNC)) {
            result.add("O_DSYNC");
        }
        if (BitTest.test(flags, FASYNC)) {
            result.add("FASYNC");
        }
        if (BitTest.test(flags, O_DIRECT)) {
            result.add("O_DIRECT");
        }
        if (BitTest.test(flags, O_LARGEFILE)) {
            result.add("O_LARGEFILE");
        }
        if (BitTest.test(flags, O_DIRECTORY)) {
            result.add("O_DIRECTORY");
        }
        if (BitTest.test(flags, O_NOFOLLOW)) {
            result.add("O_NOFOLLOW");
        }
        if (BitTest.test(flags, O_NOATIME)) {
            result.add("O_NOATIME");
        }
        if (BitTest.test(flags, O_CLOEXEC)) {
            result.add("O_CLOEXEC");
        }
        if (BitTest.test(flags, O_TMPFILE)) {
            result.add("O_TMPFILE");
        }
        if (result.size() == 0) {
            return "0";
        } else {
            return result.stream().collect(Collectors.joining("|"));
        }
    }

    public static String fcntl(int cmd) {
        switch (cmd) {
            case Fcntl.F_GETFD:
                return "F_GETFD";
            case Fcntl.F_SETFD:
                return "F_SETFD";
            case Fcntl.F_GETFL:
                return "F_GETFL";
            case Fcntl.F_SETFL:
                return "F_SETFL";
            case Fcntl.F_DUPFD:
                return "F_DUPFD";
            case Fcntl.F_DUPFD_CLOEXEC:
                return "F_DUPFD_CLOEXEC";
            default:
                return Integer.toString(cmd);
        }
    }
}
