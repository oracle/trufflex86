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
package org.graalvm.vm.x86.emu;

import java.io.Closeable;
import java.io.IOException;

import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.x86.posix.Syscalls;

public class Ptrace implements Closeable {
    private static native int fork(String cmd) throws PosixException;

    private static native int waitForSignal(int pid) throws PosixException;

    private static native long syscall(int pid, long nr, long a1, long a2, long a3, long a4, long a5, long a6) throws PosixException;

    private static native int step(int pid) throws PosixException;

    private static native long read(int pid, long addr) throws PosixException;

    private static native void write(int pid, long addr, long value) throws PosixException;

    private static native void kill(int pid, int signal) throws PosixException;

    private static native void readRegisters(int pid, Registers regs) throws PosixException;

    private static native void writeRegisters(int pid, Registers regs) throws PosixException;

    private final int pid;
    private boolean exited;

    public Ptrace() throws IOException, PosixException {
        this("/bin/sh"); // always available
    }

    public Ptrace(String name) throws PosixException, IOException {
        exited = false;
        pid = fork(name);
        clearMaps();
    }

    private void clearMaps() throws IOException, PosixException {
        // remove all mappings and map one page to 0x10000
        MemoryMap map = new MemoryMap(getPid());
        Registers regs = getRegisters();
        long ptr = mmap(0x10000, 4096, true, true, true, true, true, false, -1, 0);
        if (ptr != 0x10000) {
            throw new RuntimeException("cannot map memory!");
        }
        regs.rip = ptr;
        setRegisters(regs);
        for (MemorySegment s : map.getSegments()) {
            if (!s.contains(regs.rip)) {
                munmap(s.start, s.length);
            }
        }
    }

    public long read(long addr) throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            return read(pid, addr);
        }
    }

    public void write(long addr, long value) throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            write(pid, addr, value);
        }
    }

    public long syscall(int nr, long a1, long a2, long a3, long a4, long a5, long a6) throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            return syscall(pid, nr, a1, a2, a3, a4, a5, a6);
        }
    }

    public boolean step() throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            boolean next = step(pid) != 0;
            if (!next) {
                exited = true;
            }
            return next;
        }
    }

    public Registers getRegisters() throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            Registers regs = new Registers();
            readRegisters(pid, regs);
            return regs;
        }
    }

    public void setRegisters(Registers regs) throws PosixException {
        if (exited) {
            throw new IllegalStateException("process already exited");
        } else {
            writeRegisters(pid, regs);
        }
    }

    public int getPid() {
        return pid;
    }

    @Override
    public void close() {
        if (!exited) {
            try {
                kill(pid, 9); // 9 = SIGKILL
                exited = true;
            } catch (PosixException e) {
                e.printStackTrace();
            }
        }
    }

    public static final int PROT_READ = 0x00000001;
    public static final int PROT_WRITE = 0x00000002;
    public static final int PROT_EXEC = 0x00000004;
    public static final int MAP_FIXED = 0x00000010;
    public static final int MAP_ANONYMOUS = 0x00000020;
    public static final int MAP_SHARED = 0x00000001;
    public static final int MAP_PRIVATE = 0x00000002;

    private long sc(int nr, long a1, long a2, long a3, long a4, long a5, long a6) throws PosixException {
        long result = syscall(nr, a1, a2, a3, a4, a5, a6);
        if (result < 0) {
            throw new PosixException((int) -result);
        } else {
            return result;
        }
    }

    public long mmap(long addr, long len, int prot, int flags, int fildes, long off) throws PosixException {
        return sc(Syscalls.SYS_mmap, addr, len, prot, flags, fildes, off);
    }

    public long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException {
        int prot = 0;
        if (r) {
            prot |= PROT_READ;
        }
        if (w) {
            prot |= PROT_WRITE;
        }
        if (x) {
            prot |= PROT_EXEC;
        }
        int flags = 0;
        if (fixed) {
            flags |= MAP_FIXED;
        }
        if (anonymous) {
            flags |= MAP_ANONYMOUS;
        }
        if (shared) {
            flags |= MAP_SHARED;
        } else {
            flags |= MAP_PRIVATE;
        }
        return sc(Syscalls.SYS_mmap, addr, len, prot, flags, fildes, off);
    }

    public int munmap(long addr, long len) throws PosixException {
        return (int) sc(Syscalls.SYS_munmap, addr, len, 0, 0, 0, 0);
    }

    public int mprotect(long addr, long len, int prot) throws PosixException {
        return (int) sc(Syscalls.SYS_mprotect, addr, len, prot, 0, 0, 0);
    }

    public int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException {
        int prot = 0;
        if (r) {
            prot |= PROT_READ;
        }
        if (w) {
            prot |= PROT_WRITE;
        }
        if (x) {
            prot |= PROT_EXEC;
        }
        return (int) sc(Syscalls.SYS_mprotect, addr, len, prot, 0, 0, 0);
    }

}
