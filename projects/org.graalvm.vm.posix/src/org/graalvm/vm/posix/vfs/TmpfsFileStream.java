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
package org.graalvm.vm.posix.vfs;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;

public class TmpfsFileStream extends Stream {
    private int pos;
    private TmpfsFile file;
    private boolean r;
    private boolean w;

    public TmpfsFileStream(TmpfsFile file, boolean read, boolean write) {
        this.file = file;
        this.r = read;
        this.w = write;
        pos = 0;
        if (read && !write) {
            statusFlags = Fcntl.O_RDONLY;
        } else if (!read && write) {
            statusFlags = Fcntl.O_WRONLY;
        } else if (read && write) {
            statusFlags = Fcntl.O_RDWR;
        }
    }

    @Override
    public int read(byte[] buf, int offset, int length) throws PosixException {
        if (!r) {
            throw new PosixException(Errno.EBADF);
        }
        int bytes = file.read(pos, buf, offset, length);
        pos += bytes;
        return bytes;
    }

    @Override
    public int write(byte[] buf, int offset, int length) throws PosixException {
        if (!w) {
            throw new PosixException(Errno.EBADF);
        }
        file.insert(pos, buf, offset, length);
        pos += length;
        return length;
    }

    @Override
    public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
        if (!r) {
            throw new PosixException(Errno.EBADF);
        }
        if ((int) fileOffset != fileOffset) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        int bytes = file.read((int) fileOffset, buf, offset, length);
        return bytes;
    }

    @Override
    public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
        if (!w) {
            throw new PosixException(Errno.EBADF);
        }
        if ((int) fileOffset != fileOffset) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        file.insert((int) fileOffset, buf, offset, length);
        return length;
    }

    @Override
    public int close() throws PosixException {
        return 0;
    }

    @Override
    public long lseek(long offset, int whence) throws PosixException {
        long newpos;
        switch (whence) {
            case SEEK_SET:
                newpos = offset;
                break;
            case SEEK_CUR:
                newpos = pos + offset;
                break;
            case SEEK_END:
                newpos = file.size() + offset;
                break;
            default:
                throw new PosixException(Errno.EINVAL);
        }
        if (offset > 0 && newpos < 0) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        if (newpos < 0) {
            throw new PosixException(Errno.EINVAL);
        }
        // truncate to int
        if ((int) newpos != newpos) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        pos = (int) newpos;
        return pos;
    }

    @Override
    public void stat(Stat buf) throws PosixException {
        file.stat(buf);
    }

    @Override
    public void ftruncate(long length) throws PosixException {
        if (length != (int) length) {
            throw new PosixException(Errno.EFBIG);
        }
        file.truncate((int) length);
    }

    @Override
    public PosixPointer mmap(long size, int prot, int flags, long off) throws PosixException {
        return new TmpfsFileMemory(file, off);
    }
}
