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
package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.vfs.FileSystem;
import org.graalvm.vm.posix.vfs.Tmpfs;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.junit.Before;
import org.junit.Test;

public class ElfLoaderTest {
    private JavaVirtualMemory mem;
    private PosixEnvironment env;
    private ElfLoader loader;

    long sp = 0x7ffffff0;

    @Before
    public void setup() throws Exception {
        byte[] program = TestDataLoader.loadFile("bin/helloworld.elf");

        mem = new JavaVirtualMemory();
        Memory stackMemory = new ByteMemory(4 * 1024, false);
        MemoryPage stack = new MemoryPage(stackMemory, 0x7ffff000, stackMemory.size(), "[stack]");
        mem.add(stack);

        // setup posix environment and create executable at /tmp/test
        env = new PosixEnvironment(mem, "x86_64", null);
        FileSystem tmpfs = new Tmpfs(env.getVFS());
        Posix posix = env.getPosix();
        posix.mkdir("/tmp", 0755);
        env.mount("/tmp", tmpfs);
        posix.chdir("/tmp");
        int fd = posix.open("./test", Fcntl.O_CREAT | Fcntl.O_WRONLY, 0755);
        BytePosixPointer ptr = new BytePosixPointer(program);
        posix.write(fd, ptr, program.length);
        posix.close(fd);

        loader = new ElfLoader(null);
        Map<String, String> environ = new HashMap<>();
        environ.put("PATH", "/bin:/usr/bin");
        loader.setEnvironment(environ);
        loader.setVirtualMemory(mem);
        loader.setSP(sp);
        loader.setPosixEnvironment(env);
    }

    private String readPointer(long ptr) {
        long p = mem.getI64(ptr);
        return readString(p);
    }

    private String readString(long ptr) {
        return CString.cstr(new PosixVirtualMemoryPointer(mem, ptr));
    }

    private String readString(long ptr, int length) {
        byte[] buf = new byte[length];
        long p = ptr;
        for (int i = 0; i < length; i++) {
            buf[i] = mem.getI8(p++);
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    @Test
    public void testLoad() throws Exception {
        // load ./test with arguments "hello" and "world"
        loader.setArguments("./test", "hello", "world");
        loader.setProgramName("./test");
        loader.load("./test");

        // metadata
        assertTrue(loader.isAMD64());
        assertEquals(0x4000b0, loader.getPC());
        assertNotNull(loader.getSymbols());
        assertNotNull(loader.getSymbols().get(0x4000b0L));
        assertEquals("_start", loader.getSymbols().get(0x4000b0L).getName());

        // start of program code (.text)
        assertEquals((byte) 0x31, mem.getI8(0x4000b0));
        assertEquals((byte) 0xc0, mem.getI8(0x4000b1));
        assertEquals((byte) 0xff, mem.getI8(0x4000b2));

        // start of data (.data)
        assertEquals('H', mem.getI8(0x6000cd));
        assertEquals('e', mem.getI8(0x6000ce));
        assertEquals('l', mem.getI8(0x6000cf));
        assertEquals('l', mem.getI8(0x6000d0));
        assertEquals('o', mem.getI8(0x6000d1));

        String helloWorld = readString(0x6000cd, 13);
        assertEquals("Hello world!\n", helloWorld);

        // page permissions
        MemoryPage text = mem.get(loader.getPC());
        assertNotNull(text);
        assertTrue(text.r);
        assertFalse(text.w);
        assertTrue(text.x);

        MemoryPage data = mem.get(0x6000cd);
        assertNotNull(data);
        assertTrue(data.r);
        assertTrue(data.w);
        assertFalse(data.x);
    }

    @Test
    public void testArguments() throws Exception {
        // load ./test with arguments "hello" and "world"
        loader.setArguments("./test", "hello", "world");
        loader.setProgramName("./test");
        loader.load("./test");

        long ptr = loader.getSP();
        assertTrue(ptr < sp);

        // argc
        long argc = mem.getI64(ptr);
        assertEquals(3, argc);

        // argv
        long argv = ptr += 8;

        String arg0 = readPointer(argv);
        assertEquals("./test", arg0);
        argv += 8;
        String arg1 = readPointer(argv);
        assertEquals("hello", arg1);
        argv += 8;
        String arg2 = readPointer(argv);
        assertEquals("world", arg2);
        argv += 8;
        assertEquals(0, mem.getI64(argv));

        // envp
        long envp = argv + 8;
        String path = readPointer(envp);
        assertEquals("PATH=/bin:/usr/bin", path);
        envp += 8;
        assertEquals(0, mem.getI64(envp));
    }
}
