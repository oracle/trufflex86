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
package org.graalvm.vm.posix.api.linux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.util.BitTest;

public class Sched {
    // @formatter:off
    public static final int CSIGNAL       = 0x000000ff; /* Signal mask to be sent at exit. */
    public static final int CLONE_VM      = 0x00000100; /* Set if VM shared between processes. */
    public static final int CLONE_FS      = 0x00000200; /* Set if fs info shared between processes. */
    public static final int CLONE_FILES   = 0x00000400; /* Set if open files shared between processes. */
    public static final int CLONE_SIGHAND = 0x00000800; /* Set if signal handlers shared. */
    public static final int CLONE_PTRACE  = 0x00002000; /* Set if tracing continues on the child. */
    public static final int CLONE_VFORK   = 0x00004000; /* Set if the parent wants the child to wake it up on mm_release. */
    public static final int CLONE_PARENT  = 0x00008000; /* Set if we want to have the same parent as the cloner. */
    public static final int CLONE_THREAD  = 0x00010000; /* Set to add to same thread group. */
    public static final int CLONE_NEWNS   = 0x00020000; /* Set to create new namespace. */
    public static final int CLONE_SYSVSEM = 0x00040000; /* Set to shared SVID SEM_UNDO semantics. */
    public static final int CLONE_SETTLS  = 0x00080000; /* Set TLS info. */
    public static final int CLONE_PARENT_SETTID = 0x00100000; /* Store TID in userlevel buffer before MM copy. */
    public static final int CLONE_CHILD_CLEARTID = 0x00200000; /* Register exit futex and memory location to clear. */
    public static final int CLONE_DETACHED = 0x00400000; /* Create clone detached. */
    public static final int CLONE_UNTRACED = 0x00800000; /* Set if the tracing process can't force CLONE_PTRACE on this clone. */
    public static final int CLONE_CHILD_SETTID = 0x01000000; /* Store TID in userlevel buffer in the child. */
    public static final int CLONE_NEWCGROUP    = 0x02000000;  /* New cgroup namespace. */
    public static final int CLONE_NEWUTS   = 0x04000000;      /* New utsname group. */
    public static final int CLONE_NEWIPC   = 0x08000000;      /* New ipcs. */
    public static final int CLONE_NEWUSER  = 0x10000000;      /* New user namespace. */
    public static final int CLONE_NEWPID   = 0x20000000;      /* New pid namespace. */
    public static final int CLONE_NEWNET   = 0x40000000;      /* New network namespace. */
    public static final int CLONE_IO       = 0x80000000;      /* Clone I/O context. */
    // @formatter:on

    public static final String clone(int flags) {
        List<String> result = new ArrayList<>();
        if (BitTest.test(flags, CLONE_VM)) {
            result.add("CLONE_VM");
        }
        if (BitTest.test(flags, CLONE_FS)) {
            result.add("CLONE_FS");
        }
        if (BitTest.test(flags, CLONE_FILES)) {
            result.add("CLONE_FILES");
        }
        if (BitTest.test(flags, CLONE_SIGHAND)) {
            result.add("CLONE_SIGHAND");
        }
        if (BitTest.test(flags, CLONE_PTRACE)) {
            result.add("CLONE_PTRACE");
        }
        if (BitTest.test(flags, CLONE_VFORK)) {
            result.add("CLONE_VFORK");
        }
        if (BitTest.test(flags, CLONE_PARENT)) {
            result.add("CLONE_PARENT");
        }
        if (BitTest.test(flags, CLONE_THREAD)) {
            result.add("CLONE_THREAD");
        }
        if (BitTest.test(flags, CLONE_NEWNS)) {
            result.add("CLONE_NEWNS");
        }
        if (BitTest.test(flags, CLONE_SYSVSEM)) {
            result.add("CLONE_SYSVSEM");
        }
        if (BitTest.test(flags, CLONE_SETTLS)) {
            result.add("CLONE_SETTLS");
        }
        if (BitTest.test(flags, CLONE_PARENT_SETTID)) {
            result.add("CLONE_PARENT_SETTID");
        }
        if (BitTest.test(flags, CLONE_CHILD_CLEARTID)) {
            result.add("CLONE_CHILD_CLEARTID");
        }
        if (BitTest.test(flags, CLONE_DETACHED)) {
            result.add("CLONE_DETACHED");
        }
        if (BitTest.test(flags, CLONE_UNTRACED)) {
            result.add("CLONE_UNTRACED");
        }
        if (BitTest.test(flags, CLONE_CHILD_SETTID)) {
            result.add("CLONE_CHILD_SETTID");
        }
        if (BitTest.test(flags, CLONE_NEWCGROUP)) {
            result.add("CLONE_NEWCGROUP");
        }
        if (BitTest.test(flags, CLONE_NEWUTS)) {
            result.add("CLONE_NEWUTS");
        }
        if (BitTest.test(flags, CLONE_NEWIPC)) {
            result.add("CLONE_NEWIPC");
        }
        if (BitTest.test(flags, CLONE_NEWUSER)) {
            result.add("CLONE_NEWUSER");
        }
        if (BitTest.test(flags, CLONE_NEWPID)) {
            result.add("CLONE_NEWPID");
        }
        if (BitTest.test(flags, CLONE_NEWNET)) {
            result.add("CLONE_NEWNET");
        }
        if (BitTest.test(flags, CLONE_IO)) {
            result.add("CLONE_IO");
        }
        return result.stream().collect(Collectors.joining("|"));
    }
}
