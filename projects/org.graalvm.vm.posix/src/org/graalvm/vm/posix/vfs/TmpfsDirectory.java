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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.PosixException;

public class TmpfsDirectory extends VFSDirectory {
    private Map<String, VFSEntry> files;
    private Date atime;
    private Date mtime;
    private Date ctime;

    public TmpfsDirectory(VFS vfs, String path, long uid, long gid, long permissions) {
        super(vfs, path, uid, gid, permissions);
        files = new HashMap<>();
        atime = new Date();
        mtime = atime;
        ctime = atime;
    }

    public TmpfsDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
        super(parent, path, uid, gid, permissions);
        files = new HashMap<>();
        atime = new Date();
        mtime = atime;
        ctime = atime;
    }

    @Override
    protected void create(VFSEntry file) {
        mtime = new Date();
        files.put(file.getName(), file);
    }

    @Override
    protected VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException {
        TmpfsDirectory dir = new TmpfsDirectory(this, name, uid, gid, permissions);
        create(dir);
        return dir;
    }

    @Override
    protected TmpfsFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
        TmpfsFile file = new TmpfsFile(this, name, uid, gid, permissions);
        create(file);
        return file;
    }

    @Override
    protected VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target) throws PosixException {
        TmpfsSymlink symlink = new TmpfsSymlink(this, name, uid, gid, permissions, target);
        create(symlink);
        return symlink;
    }

    @Override
    protected void delete(String name) {
        mtime = new Date();
        files.remove(name);
    }

    @Override
    protected VFSEntry getEntry(String name) {
        atime = new Date();
        return files.get(name);
    }

    @Override
    protected List<VFSEntry> list() {
        atime = new Date();
        return files.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public long size() {
        return files.size();
    }

    @Override
    public void atime(Date time) {
        this.atime = time;
    }

    @Override
    public Date atime() {
        return atime;
    }

    @Override
    public void mtime(Date time) {
        this.mtime = time;
    }

    @Override
    public Date mtime() {
        return mtime;
    }

    @Override
    public void ctime(Date time) {
        this.ctime = time;
    }

    @Override
    public Date ctime() {
        return ctime;
    }

    @Override
    public String toString() {
        return "TmpfsDirectory[" + files.entrySet().stream().map(Map.Entry::getValue).map(Object::toString).collect(Collectors.joining(",")) + "]";
    }
}
