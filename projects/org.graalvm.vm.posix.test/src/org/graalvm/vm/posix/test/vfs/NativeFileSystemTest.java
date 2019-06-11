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
package org.graalvm.vm.posix.test.vfs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.NativeFileSystem;
import org.graalvm.vm.posix.vfs.Tmpfs;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.junit.Test;

public class NativeFileSystemTest {
    @Test
    public void testRoot() throws PosixException, IOException {
        NativeFileSystem fs = new NativeFileSystem(null, "/");
        long atime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastAccessTime")).toMillis();
        long mtime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastModifiedTime")).toMillis();
        long ctime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:ctime")).toMillis();
        VFSDirectory root = fs.getRoot();
        assertEquals(0, root.getUID());
        assertEquals(0, root.getGID());
        assertEquals(mtime, root.mtime().getTime());
        assertEquals(atime, root.atime().getTime());
        assertEquals(ctime, root.ctime().getTime());
        assertEquals(0755, root.getPermissions() & 0777);
    }

    @Test
    public void testProc001() throws PosixException {
        NativeFileSystem fs = new NativeFileSystem(null, "/proc");
        VFSDirectory root = fs.getRoot();
        List<VFSEntry> entries = root.readdir();
        assertEquals(1, entries.stream().filter((x) -> x.getName().equals("cpuinfo")).count());
    }

    @Test
    public void testMount001() throws PosixException, IOException {
        NativeFileSystem fs = new NativeFileSystem(null, "/");
        VFS vfs = new VFS();
        vfs.mount("/", fs);

        BasicFileAttributes info = Files.getFileAttributeView(Paths.get("/proc"), BasicFileAttributeView.class).readAttributes();

        Stat buf = new Stat();
        vfs.stat("/proc", buf);

        assertEquals(info.size(), buf.st_size);
        assertEquals(info.lastModifiedTime().toMillis(), buf.st_mtim.toMillis());

        byte[] ref = Files.readAllBytes(Paths.get("/proc/cpuinfo"));
        byte[] act = new byte[4096];
        Stream in = vfs.open("/proc/cpuinfo", Fcntl.O_RDONLY, 0);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        int n;
        while ((n = in.read(act, 0, 4096)) > 0) {
            tmp.write(act, 0, n);
        }
        tmp.close();
        act = tmp.toByteArray();
        assertEquals(ref.length, act.length);
        in.close();
        assertArrayEquals(ref, act);
    }

    @Test
    public void testSymlink001() throws PosixException, IOException {
        NativeFileSystem fs = new NativeFileSystem(null, "/");
        VFS vfs = new VFS();
        vfs.mount("/", fs);

        byte[] ref = Files.readAllBytes(Paths.get("/proc/self/cmdline"));
        byte[] act = new byte[ref.length];
        Stream in = vfs.open("/proc/self/cmdline", Fcntl.O_RDONLY, 0);
        assertEquals(act.length, in.read(act, 0, act.length));
        in.close();
        assertArrayEquals(ref, act);
    }

    @Test
    public void testSymlink002() throws PosixException, IOException {
        NativeFileSystem fs = new NativeFileSystem(null, "/");
        VFS vfs = new VFS();
        vfs.mount("/", fs);

        String ref = Files.readSymbolicLink(Paths.get("/proc/self/exe")).toString();
        String act = vfs.readlink("/proc/self/exe");
        assertEquals(ref, act);
    }

    @Test
    public void testMount002() throws PosixException {
        VFS vfs = new VFS();
        NativeFileSystem fs = new NativeFileSystem(vfs, "/");
        vfs.mount("/", fs);

        Tmpfs tmpfs = new Tmpfs(vfs);
        vfs.mount("/proc", tmpfs);

        List<VFSEntry> entries = vfs.list("/proc");
        assertEquals(0, entries.size());
    }
}
