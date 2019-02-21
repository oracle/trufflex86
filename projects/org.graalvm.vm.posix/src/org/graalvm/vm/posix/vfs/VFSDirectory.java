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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.Dirent;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;

public abstract class VFSDirectory extends VFSEntry {
    private VFSDirectory mount;

    public VFSDirectory(VFS vfs, String path, long uid, long gid, long permissions) {
        super(vfs, path, uid, gid, permissions);
        mount = null;
    }

    public VFSDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
        super(parent, path, uid, gid, permissions);
        mount = null;
    }

    public void mount(VFSDirectory fs) {
        mount = fs;
    }

    public void umount(VFSDirectory fs) {
        assert mount == fs;
        mount = null;
    }

    protected abstract void create(VFSEntry file) throws PosixException;

    protected abstract VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException;

    protected abstract VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException;

    protected abstract VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target) throws PosixException;

    protected abstract void delete(String name) throws PosixException;

    protected abstract VFSEntry getEntry(String name) throws PosixException;

    protected abstract List<VFSEntry> list() throws PosixException;

    protected Stream opendir(@SuppressWarnings("unused") int flags, @SuppressWarnings("unused") int mode) throws PosixException {
        List<Dirent> entries = new ArrayList<>();
        Dirent self = new Dirent();
        Stat stat = new Stat();
        stat(stat);
        self.d_ino = stat.st_ino;
        self.d_off = 0;
        self.d_type = Dirent.DT_DIR;
        self.d_name = ".";
        entries.add(self);
        Dirent parent = new Dirent();
        parent.d_ino = self.d_ino;
        parent.d_off = 1;
        parent.d_type = Dirent.DT_DIR;
        parent.d_name = "..";
        entries.add(parent);

        long off = 2;
        for (VFSEntry entry : list()) {
            Dirent dirent = new Dirent();
            entry.stat(stat);
            dirent.d_ino = stat.st_ino;
            dirent.d_off = off;
            dirent.d_type = Dirent.IFTODT(stat.st_mode);
            dirent.d_name = entry.getName();
            entries.add(dirent);
            off++;
        }
        return new GenericDirectoryStream(this, entries.iterator());
    }

    public final Stream open(int flags, int mode) throws PosixException {
        if (mount != null) {
            return mount.open(flags, mode);
        } else {
            return opendir(flags, mode);
        }
    }

    public final VFSDirectory mkdir(String name, long uid, long gid, long permissions) throws PosixException {
        if (mount != null) {
            return mount.mkdir(name, uid, gid, permissions);
        } else {
            return createDirectory(name, uid, gid, permissions);
        }
    }

    public final VFSFile mkfile(String name, long uid, long gid, long permissions) throws PosixException {
        if (mount != null) {
            return mount.mkfile(name, uid, gid, permissions);
        } else {
            return createFile(name, uid, gid, permissions);
        }
    }

    public final VFSSymlink symlink(String name, long uid, long gid, long permissions, String target)
                    throws PosixException {
        if (mount != null) {
            return mount.createSymlink(name, uid, gid, permissions, target);
        } else {
            return createSymlink(name, uid, gid, permissions, target);
        }
    }

    public final void unlink(String name) throws PosixException {
        if (mount != null) {
            mount.unlink(name);
        } else {
            delete(name);
        }
    }

    public final List<VFSEntry> readdir() throws PosixException {
        if (mount != null) {
            return mount.readdir();
        } else {
            return list();
        }
    }

    public final VFSEntry get(String name) throws PosixException {
        if (mount != null) {
            return mount.get(name);
        } else {
            return getEntry(name);
        }
    }

    @Override
    public void stat(Stat buf) throws PosixException {
        super.stat(buf);
        buf.st_mode |= Stat.S_IFDIR;
    }

    @Override
    public String toString() {
        try {
            return "VFSDirectory[" + list().stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
        } catch (PosixException e) {
            return "VFSDirectory[" + getPath() + "]";
        }
    }
}
