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

import java.util.Date;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.Timespec;
import org.graalvm.vm.posix.api.Utimbuf;
import org.graalvm.vm.posix.api.io.Stat;

public abstract class VFSEntry {
    private String path;
    private long uid;
    private long gid;
    private long permissions;
    private VFSDirectory parent;
    private VFS vfs;

    protected VFSEntry(VFS vfs, String path) {
        this.vfs = vfs;
        if (path.startsWith("/")) {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
    }

    protected VFSEntry(VFS vfs, String path, long uid, long gid, long permissions) {
        this(vfs, path);
        this.uid = uid;
        this.gid = gid;
        this.permissions = permissions;
    }

    protected VFSEntry(VFSDirectory parent, String path, long uid, long gid, long permissions) {
        this(parent, path);
        this.uid = uid;
        this.gid = gid;
        this.permissions = permissions;
    }

    protected VFSEntry(VFSDirectory parent, String path) {
        this((VFS) null, path);
        this.parent = parent;
    }

    protected VFS getVFS() {
        if (vfs == null && parent != null) {
            return parent.getVFS();
        } else {
            return vfs;
        }
    }

    public VFSDirectory getParent() {
        return parent;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public String getEntryPath() {
        int last = path.lastIndexOf("/");
        if (last != -1) {
            return path.substring(0, last);
        } else {
            return "";
        }
    }

    @SuppressWarnings("unused")
    public void chmod(int mode) throws PosixException {
        permissions = mode;
    }

    @SuppressWarnings("unused")
    public void chown(long owner, long group) throws PosixException {
        uid = owner;
        gid = group;
    }

    @SuppressWarnings("unused")
    public long getUID() throws PosixException {
        return uid;
    }

    @SuppressWarnings("unused")
    public long getGID() throws PosixException {
        return gid;
    }

    @SuppressWarnings("unused")
    public long getPermissions() throws PosixException {
        return permissions;
    }

    public abstract long size() throws PosixException;

    public abstract void atime(Date time) throws PosixException;

    public abstract void mtime(Date time) throws PosixException;

    public abstract void ctime(Date time) throws PosixException;

    public abstract Date atime() throws PosixException;

    public abstract Date mtime() throws PosixException;

    public abstract Date ctime() throws PosixException;

    public void utime(Utimbuf times) throws PosixException {
        long atime;
        long mtime;
        if (times == null) {
            atime = new Date().getTime() / 1000;
            mtime = atime;
        } else {
            atime = times.actime;
            mtime = times.modtime;
        }
        atime(new Date(atime * 1000));
        mtime(new Date(mtime * 1000));
    }

    public void stat(Stat buf) throws PosixException {
        buf.st_dev = 0; // TODO
        buf.st_ino = 1; // TODO
        buf.st_mode = (int) getPermissions(); // TODO
        buf.st_nlink = 0; // TODO
        buf.st_uid = (int) getUID();
        buf.st_gid = (int) getGID();
        buf.st_rdev = 0; // TODO
        buf.st_size = size();
        buf.st_blksize = 4096; // TODO
        buf.st_blocks = (long) Math.ceil(buf.st_size / 512.0); // TODO
        buf.st_atim = new Timespec(atime());
        buf.st_mtim = new Timespec(mtime());
        buf.st_ctim = new Timespec(ctime());
    }

    @Override
    public String toString() {
        return "VFSEntry[" + path + "]";
    }
}
