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
package org.graalvm.vm.posix.api.mem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.util.BitTest;

public class Mman {
    // @formatter:off
    public static final int PROT_READ         = 0x1;           /* page can be read */
    public static final int PROT_WRITE        = 0x2;           /* page can be written */
    public static final int PROT_EXEC         = 0x4;           /* page can be executed */
    public static final int PROT_SEM          = 0x8;           /* page may be used for atomic ops */
    public static final int PROT_NONE         = 0x0;           /* page can not be accessed */
    public static final int PROT_GROWSDOWN    = 0x01000000;    /* mprotect flag: extend change to start of growsdown vma */
    public static final int PROT_GROWSUP      = 0x02000000;    /* mprotect flag: extend change to end of growsup vma */

    public static final int MAP_SHARED        = 0x01;          /* Share changes */
    public static final int MAP_PRIVATE       = 0x02;          /* Changes are private */
    public static final int MAP_TYPE          = 0x0f;          /* Mask for type of mapping */
    public static final int MAP_FIXED         = 0x10;          /* Interpret addr exactly */
    public static final int MAP_ANONYMOUS     = 0x20;          /* don't use a file */
    public static final int MAP_UNINITIALIZED = 0x4000000;     /* For anonymous mmap, memory could be uninitialized */

    /*
     * Flags for mlock
     */
    public static final int MLOCK_ONFAULT     = 0x01;          /* Lock pages in range after they are faulted in, do not prefault */

    public static final int MS_ASYNC          = 1;             /* sync memory asynchronously */
    public static final int MS_INVALIDATE     = 2;             /* invalidate the caches */
    public static final int MS_SYNC           = 4;             /* synchronous memory sync */

    public static final int MADV_NORMAL       = 0;             /* no further special treatment */
    public static final int MADV_RANDOM       = 1;             /* expect random page references */
    public static final int MADV_SEQUENTIAL   = 2;             /* expect sequential page references */
    public static final int MADV_WILLNEED     = 3;             /* will need these pages */
    public static final int MADV_DONTNEED     = 4;             /* don't need these pages */

    /* common parameters: try to keep these consistent across architectures */
    public static final int MADV_FREE         = 8;             /* free pages only if memory pressure */
    public static final int MADV_REMOVE       = 9;             /* remove these pages & resources */
    public static final int MADV_DONTFORK     = 10;            /* don't inherit across fork */
    public static final int MADV_DOFORK       = 11;            /* do inherit across fork */
    public static final int MADV_HWPOISON     = 100;           /* poison a page for testing */
    public static final int MADV_SOFT_OFFLINE = 101;           /* soft offline page for testing */

    public static final int MADV_MERGEABLE    = 12;            /* KSM may merge identical pages */
    public static final int MADV_UNMERGEABLE  = 13;            /* KSM may not merge identical pages */

    public static final int MADV_HUGEPAGE     = 14;            /* Worth backing with hugepages */
    public static final int MADV_NOHUGEPAGE   = 15;            /* Not worth backing with hugepages */

    public static final int MADV_DONTDUMP     = 16;            /* Explicity exclude from the core dump, overrides the coredump filter bits */
    public static final int MADV_DODUMP       = 17;            /* Clear the MADV_DONTDUMP flag */

    /* compatibility flags */
    public static final int MAP_FILE          = 0;

    public static final int PROT_SAO          = 0x10;          /* Strong Access Ordering */

    public static final int MAP_RENAME        = MAP_ANONYMOUS; /* In SunOS terminology */
    public static final int MAP_NORESERVE     = 0x40;          /* don't reserve swap pages */
    public static final int MAP_LOCKED        = 0x80;

    public static final int MAP_GROWSDOWN     = 0x0100;        /* stack-like segment */
    public static final int MAP_DENYWRITE     = 0x0800;        /* ETXTBSY */
    public static final int MAP_EXECUTABLE    = 0x1000;        /* mark it as an executable */

    public static final int MCL_CURRENT       = 0x2000;        /* lock all currently mapped pages */
    public static final int MCL_FUTURE        = 0x4000;        /* lock all additions to address space */
    public static final int MCL_ONFAULT       = 0x8000;        /* lock all pages that are faulted in */

    public static final int MAP_POPULATE      = 0x8000;        /* populate (prefault) pagetables */
    public static final int MAP_NONBLOCK      = 0x10000;       /* do not block on IO */
    public static final int MAP_STACK         = 0x20000;       /* give out an address that is best suited for process/thread stacks */
    public static final int MAP_HUGETLB       = 0x40000;       /* create a huge page mapping */
    // @formatter:on

    public static String prot(int prot) {
        List<String> result = new ArrayList<>();
        if (BitTest.test(prot, PROT_READ)) {
            result.add("PROT_READ");
        }
        if (BitTest.test(prot, PROT_WRITE)) {
            result.add("PROT_WRITE");
        }
        if (BitTest.test(prot, PROT_EXEC)) {
            result.add("PROT_EXEC");
        }
        if (BitTest.test(prot, PROT_SEM)) {
            result.add("PROT_SEM");
        }
        if (prot == PROT_NONE) {
            result.add("PROT_NONE");
        }
        if (BitTest.test(prot, PROT_GROWSDOWN)) {
            result.add("PROT_GROWSDOWN");
        }
        if (BitTest.test(prot, PROT_GROWSUP)) {
            result.add("PROT_GROWSUP");
        }
        return result.stream().collect(Collectors.joining("|"));
    }

    public static String flags(int flags) {
        List<String> result = new ArrayList<>();
        if (BitTest.test(flags, MAP_SHARED)) {
            result.add("MAP_SHARED");
        }
        if (BitTest.test(flags, MAP_PRIVATE)) {
            result.add("MAP_PRIVATE");
        }
        if (BitTest.test(flags, MAP_FIXED)) {
            result.add("MAP_FIXED");
        }
        if (BitTest.test(flags, MAP_ANONYMOUS)) {
            result.add("MAP_ANONYMOUS");
        }
        if (BitTest.test(flags, MAP_UNINITIALIZED)) {
            result.add("MAP_UNINITIALIZED");
        }
        return result.stream().collect(Collectors.joining("|"));
    }
}
