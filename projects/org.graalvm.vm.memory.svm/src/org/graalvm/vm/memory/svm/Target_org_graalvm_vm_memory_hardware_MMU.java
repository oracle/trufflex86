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
package org.graalvm.vm.memory.svm;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.posix.headers.Errno;
import com.oracle.svm.core.posix.headers.Mman;

@Platforms(Platform.LINUX.class)
@TargetClass(org.graalvm.vm.memory.hardware.MMU.class)
final class Target_org_graalvm_vm_memory_hardware_MMU {
    @Substitute
    private static void loadLibrary() throws UnsatisfiedLinkError {
        // nothing
    }

    @Substitute
    private static long setup(long lo, long hi) throws PosixException {
        return LibMemory.setupSegvHandler(lo, hi);
    }

    @Substitute
    public static long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException {
        int prot = PosixUtils.getProtection(r, w, x);
        int flags = 0;
        if (fixed) {
            flags |= Mman.MAP_FIXED();
        }
        if (anonymous) {
            flags |= Mman.MAP_ANONYMOUS();
        }
        if (shared) {
            flags |= Mman.MAP_SHARED();
        } else {
            flags |= Mman.MAP_PRIVATE();
        }
        PointerBase result = Mman.mmap(WordFactory.pointer(addr), WordFactory.unsigned(len), prot, flags, fildes, off);
        if (result.equal(Mman.MAP_FAILED())) {
            int errno = Errno.errno();
            throw new PosixException(ErrnoTranslator.translate(errno));
        } else {
            return result.rawValue();
        }
    }

    @Substitute
    public static int munmap(long addr, long len) throws PosixException {
        int result = Mman.munmap(WordFactory.pointer(addr), WordFactory.unsigned(len));
        if (result < 0) {
            int errno = Errno.errno();
            throw new PosixException(ErrnoTranslator.translate(errno));
        }
        return result;
    }

    @Substitute
    public static int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException {
        int prot = PosixUtils.getProtection(r, w, x);
        int result = Mman.mprotect(WordFactory.pointer(addr), WordFactory.unsigned(len), prot);
        if (result < 0) {
            int errno = Errno.errno();
            throw new PosixException(ErrnoTranslator.translate(errno));
        }
        return result;
    }
}
