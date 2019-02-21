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

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.tty.TTYStream;

public class FileDescriptorManager {
    public static final int STDIN = 0;
    public static final int STDOUT = 1;
    public static final int STDERR = 2;

    private Map<Integer, FileDescriptor> fds;

    public FileDescriptorManager() {
        boolean tty = System.console() != null;
        fds = new HashMap<>();
        if (tty) {
            fds.put(STDIN, new FileDescriptor(new TTYStream(System.in)));
            fds.put(STDOUT, new FileDescriptor(new TTYStream(System.out)));
            fds.put(STDERR, new FileDescriptor(new TTYStream(System.err)));
        } else {
            fds.put(STDIN, new FileDescriptor(new PipeStream(System.in)));
            fds.put(STDOUT, new FileDescriptor(new PipeStream(System.out)));
            fds.put(STDERR, new FileDescriptor(new PipeStream(System.err)));
        }
    }

    public boolean used(int fd) {
        return fds.containsKey(fd);
    }

    public int next() {
        return next(0);
    }

    public int next(int low) {
        int fd;
        for (fd = low; used(fd); fd++) {
        }
        return fd;
    }

    public int allocate(Stream stream) {
        int fd = next();
        setStream(fd, stream);
        return fd;
    }

    public int allocate(Stream stream, int lowfd) {
        int fd = next(lowfd);
        setStream(fd, stream);
        return fd;
    }

    public void free(int fildes) {
        fds.remove(fildes);
    }

    public Stream getStream(int fildes) throws PosixException {
        FileDescriptor fd = fds.get(fildes);
        if (fd == null) {
            throw new PosixException(Errno.EBADF);
        }
        return fd.stream;
    }

    public FileDescriptor getFileDescriptor(int fildes) throws PosixException {
        FileDescriptor fd = fds.get(fildes);
        if (fd == null) {
            throw new PosixException(Errno.EBADF);
        }
        return fd;
    }

    public void setStream(int filedes, Stream stream) {
        fds.put(filedes, new FileDescriptor(stream));
    }

    public void setStream(int filedes, Stream stream, int flags) {
        fds.put(filedes, new FileDescriptor(stream, flags));
    }

    public int count() {
        return fds.size();
    }
}
