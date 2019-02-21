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
import static org.junit.Assert.assertNotNull;

import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.Tmpfs;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSFile;
import org.junit.Before;
import org.junit.Test;

public class TmpfsTest {
    private VFS vfs;
    private Tmpfs tmpfs;

    @Before
    public void setup() throws PosixException {
        vfs = new VFS();
        tmpfs = new Tmpfs(vfs);
        vfs.mkdir("/tmp", 0, 0, 0755);
        vfs.mount("/tmp", tmpfs);
    }

    @Test
    public void test001() throws PosixException {
        VFSDirectory dir = vfs.get("/tmp");
        dir.mkfile("test", 0, 0, 0644);

        VFSFile file = vfs.get("/tmp/test");
        assertNotNull(file);
        assertEquals(0, file.size());
        assertEquals(0, file.getUID());
        assertEquals(0, file.getGID());
        assertEquals(0644, file.getPermissions());
    }

    @Test
    public void test002() throws PosixException {
        VFSDirectory dir = vfs.get("/tmp");
        dir.mkfile("test", 0, 0, 0644);

        VFSFile file = vfs.get("/tmp/test");
        Stream stream = file.open(Fcntl.O_WRONLY);
        assertEquals(0, stream.lseek(0, Stream.SEEK_SET));
        assertEquals(0, stream.lseek(0, Stream.SEEK_END));
        byte[] data = "Hello world!".getBytes();
        PosixPointer ptr = new BytePosixPointer(data);
        assertEquals(data.length, stream.write(ptr, data.length));
        assertEquals(data.length, stream.lseek(0, Stream.SEEK_END));
        assertEquals(data.length, stream.lseek(0, Stream.SEEK_CUR));
        assertEquals(0, stream.close());

        stream = file.open(Fcntl.O_RDONLY);
        assertEquals(data.length, file.size());
        assertEquals(data.length, stream.lseek(0, Stream.SEEK_END));
        assertEquals(0, stream.lseek(0, Stream.SEEK_SET));
        assertEquals(0, stream.lseek(0, Stream.SEEK_CUR));

        byte[] read = new byte[data.length];
        ptr = new BytePosixPointer(read);
        assertEquals(data.length, stream.read(ptr, data.length));
        assertArrayEquals(data, read);
        assertEquals(data.length, stream.lseek(0, Stream.SEEK_CUR));
    }
}
