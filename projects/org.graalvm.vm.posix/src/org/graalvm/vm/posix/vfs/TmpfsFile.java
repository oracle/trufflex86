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

import java.util.Arrays;
import java.util.Date;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stream;

public class TmpfsFile extends VFSFile {
    private byte[] data;
    private Date atime;
    private Date mtime;
    private Date ctime;

    public TmpfsFile(VFSDirectory parent, String path, long uid, long gid, long permissions) {
        super(parent, path, uid, gid, permissions);
        atime = new Date();
        mtime = atime;
        ctime = atime;
        data = new byte[0];
    }

    public void setContent(byte[] data) {
        mtime = new Date();
        this.data = data;
    }

    public byte[] getContent() {
        return data;
    }

    int read(int pos, byte[] buf, int offset, int length) {
        int len = length;
        if (pos + length > data.length) {
            len = data.length - pos;
        }
        if (len < 0) {
            return 0;
        }
        System.arraycopy(data, pos, buf, offset, len);
        return len;
    }

    void append(byte[] buf, int offset, int length) {
        mtime = new Date();
        int pos = data.length;
        data = Arrays.copyOf(data, data.length + length);
        System.arraycopy(buf, offset, data, pos, length);
    }

    void insert(int pos, byte[] buf, int offset, int length) {
        mtime = new Date();
        byte[] oldData = data;
        data = Arrays.copyOf(data, data.length + length);
        System.arraycopy(buf, offset, data, pos, length);
        System.arraycopy(oldData, pos, data, pos + length, oldData.length - pos);
    }

    void truncate(int length) throws PosixException {
        if (length < 0) {
            throw new PosixException(Errno.EINVAL);
        }
        this.data = Arrays.copyOf(data, length);
    }

    @Override
    public Stream open(boolean read, boolean write) throws PosixException {
        atime = new Date();
        return new TmpfsFileStream(this, read, write);
    }

    @Override
    public long size() {
        return data.length;
    }

    @Override
    public void atime(Date time) {
        atime = time;
    }

    @Override
    public Date atime() {
        return atime;
    }

    @Override
    public void mtime(Date time) {
        mtime = time;
    }

    @Override
    public Date mtime() {
        return mtime;
    }

    @Override
    public void ctime(Date time) {
        ctime = time;
    }

    @Override
    public Date ctime() {
        return ctime;
    }
}
