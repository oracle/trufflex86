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
import java.util.List;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;

public abstract class FileSystem {
    private String type;

    public FileSystem(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public final VFSDirectory createMountPoint(VFS vfs, String mountPoint) {
        final VFSDirectory root = getRoot();
        return new VFSDirectory(vfs, mountPoint, 0, 0, 0755) {
            @Override
            public Stream opendir(int flags, int mode) throws PosixException {
                return root.open(flags, mode);
            }

            @Override
            public void create(VFSEntry file) throws PosixException {
                root.create(file);
            }

            @Override
            public VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException {
                return root.createDirectory(name, uid, gid, permissions);
            }

            @Override
            public VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
                return root.createFile(name, uid, gid, permissions);
            }

            @Override
            public VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target) throws PosixException {
                return root.createSymlink(name, uid, gid, permissions, target);
            }

            @Override
            public void delete(String name) throws PosixException {
                root.delete(name);
            }

            @Override
            public VFSEntry getEntry(String name) throws PosixException {
                return root.get(name);
            }

            @Override
            public List<VFSEntry> list() throws PosixException {
                return root.list();
            }

            @Override
            public long size() throws PosixException {
                return root.size();
            }

            @Override
            public void atime(Date time) throws PosixException {
                root.atime(time);
            }

            @Override
            public Date atime() throws PosixException {
                return root.atime();
            }

            @Override
            public void mtime(Date time) throws PosixException {
                root.mtime(time);
            }

            @Override
            public Date mtime() throws PosixException {
                return root.mtime();
            }

            @Override
            public void ctime(Date time) throws PosixException {
                root.ctime(time);
            }

            @Override
            public Date ctime() throws PosixException {
                return root.ctime();
            }

            @Override
            public long getUID() throws PosixException {
                return root.getUID();
            }

            @Override
            public long getGID() throws PosixException {
                return root.getGID();
            }

            @Override
            public void stat(Stat buf) throws PosixException {
                root.stat(buf);
            }
        };
    }

    public abstract VFSDirectory getRoot();
}
