package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.junit.Before;
import org.junit.Test;

import com.everyware.posix.api.BytePosixPointer;
import com.everyware.posix.api.CString;
import com.everyware.posix.api.Posix;
import com.everyware.posix.api.io.Fcntl;
import com.everyware.posix.vfs.FileSystem;
import com.everyware.posix.vfs.Tmpfs;

public class ElfLoaderTest {
    private VirtualMemory mem;
    private PosixEnvironment env;
    private ElfLoader loader;

    long sp = 0x7ffffff0;

    @Before
    public void setup() throws Exception {
        byte[] program = TestDataLoader.loadFile("bin/helloworld.elf");

        mem = new VirtualMemory();
        Memory stackMemory = new ByteMemory(4 * 1024, false);
        MemoryPage stack = new MemoryPage(stackMemory, 0x7ffff000, stackMemory.size(), "[stack]");
        mem.add(stack);

        // setup posix environment and create executable at /tmp/test
        env = new PosixEnvironment(mem, "x86_64");
        FileSystem tmpfs = new Tmpfs(env.getVFS());
        Posix posix = env.getPosix();
        posix.mkdir("/tmp", 0755);
        env.mount("/tmp", tmpfs);
        posix.chdir("/tmp");
        int fd = posix.open("./test", Fcntl.O_CREAT | Fcntl.O_WRONLY, 0755);
        BytePosixPointer ptr = new BytePosixPointer(program);
        posix.write(fd, ptr, program.length);
        posix.close(fd);

        loader = new ElfLoader();
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
