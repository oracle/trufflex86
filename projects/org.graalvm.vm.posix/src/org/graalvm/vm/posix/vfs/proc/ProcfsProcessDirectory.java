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
package org.graalvm.vm.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.graalvm.vm.posix.vfs.VFSFile;
import org.graalvm.vm.posix.vfs.VFSSymlink;

public class ProcfsProcessDirectory extends VFSDirectory {
    private final Posix posix;

    private final Date ctime;

    public ProcfsProcessDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions, Posix posix) {
        super(parent, path, uid, gid, permissions);
        this.posix = posix;
        ctime = new Date();
    }

    @Override
    protected void create(VFSEntry file) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    protected void delete(String name) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    private VFSEntry getCwd() {
        return new ProcfsSymlink(this, "cwd", 0, 0, 0777, getVFS().getcwd());
    }

    private VFSEntry getExe() {
        return new ProcfsSymlink(this, "exe", 0, 0, 0777, posix.getExecfn());
    }

    private VFSEntry getRoot() {
        return new ProcfsSymlink(this, "root", 0, 0, 0777, "/");
    }

    private VFSEntry getTasks() {
        return new ProcfsTasksDirectory(this, "task", 0, 0, 0555, posix);
    }

    private VFSEntry getMaps() {
        return new ProcfsPidMaps(this, "maps", 0, 0, 0444, posix);
    }

    @Override
    protected VFSEntry getEntry(String name) throws PosixException {
        switch (name) {
            case "cwd":
                return getCwd();
            case "exe":
                return getExe();
            case "maps":
                return getMaps();
            case "root":
                return getRoot();
            case "task":
                return getTasks();
            default:
                throw new PosixException(Errno.ENOENT);
        }
    }

    @Override
    protected List<VFSEntry> list() throws PosixException {
        List<VFSEntry> result = new ArrayList<>();
        result.add(getCwd());
        result.add(getExe());
        result.add(getMaps());
        result.add(getRoot());
        result.add(getTasks());
        return result;
    }

    @Override
    public long size() throws PosixException {
        return 0;
    }

    @Override
    public void atime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public void mtime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public void ctime(Date time) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }

    @Override
    public Date atime() throws PosixException {
        return ctime;
    }

    @Override
    public Date mtime() throws PosixException {
        return ctime;
    }

    @Override
    public Date ctime() throws PosixException {
        return ctime;
    }
}
