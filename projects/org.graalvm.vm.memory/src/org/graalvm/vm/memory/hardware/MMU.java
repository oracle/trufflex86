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
package org.graalvm.vm.memory.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.util.UnsafeHolder;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import sun.misc.Unsafe;

public class MMU {
    private static final Logger log = Trace.create(MMU.class);

    @CompilationFinal private static boolean initialized = false;
    @CompilationFinal private static boolean loaded = false;
    private static final Unsafe unsafe = UnsafeHolder.getUnsafe();
    @CompilationFinal private static long ptr;

    public static boolean init(long lo, long hi) {
        CompilerDirectives.transferToInterpreter();
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }
        try {
            if (!loaded) {
                loadLibrary();
            }
            ptr = setup(lo, hi);
            if (ptr == 0) {
                log.log(Level.INFO, "cannot setup VM handler");
                return false;
            }
            initialized = true;
            try {
                mmap(lo, hi - lo, false, false, false, true, true, false, -1, 0);
            } catch (PosixException e) {
                log.log(Level.INFO, "cannot pre-allocate VM region");
            }
            return true;
        } catch (UnsatisfiedLinkError e) {
            log.log(Level.INFO, "cannot load libmemory");
            return false;
        } catch (PosixException e) {
            log.log(Levels.ERROR, "Error: " + Errno.toString(e.getErrno()));
            return false;
        }
    }

    public static long getSegfaultAddress() {
        if (!initialized) {
            CompilerDirectives.transferToInterpreter();
            throw new IllegalStateException("native code not initialized");
        }
        long val = unsafe.getLong(ptr);
        if (val != 0) {
            unsafe.putLong(ptr, 0);
        }
        return val;
    }

    private static void loadLibrary() throws UnsatisfiedLinkError {
        System.loadLibrary("memory");
        loaded = true;
    }

    public static void loadLibrary(String name) throws UnsatisfiedLinkError {
        System.load(name);
        loaded = true;
    }

    private static native long setup(long lo, long hi) throws PosixException;

    public static native long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException;

    public static native int munmap(long addr, long len) throws PosixException;

    public static native int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException;
}
