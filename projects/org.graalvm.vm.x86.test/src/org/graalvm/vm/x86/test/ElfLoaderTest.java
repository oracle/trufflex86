package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.PosixEnvironment;
import org.junit.Test;

import com.everyware.posix.api.BytePosixPointer;
import com.everyware.posix.api.Posix;
import com.everyware.posix.api.io.Fcntl;
import com.everyware.posix.vfs.FileSystem;
import com.everyware.posix.vfs.Tmpfs;
import com.everyware.util.log.Trace;

public class ElfLoaderTest {
    @Test
    public void test() throws Exception {
        Trace.setupConsoleApplication();
        byte[] data = TestDataLoader.loadFile("bin/helloworld.elf");

        VirtualMemory mem = new VirtualMemory();
        Memory stackMemory = new ByteMemory(8 * 1024);
        MemoryPage stack = new MemoryPage(stackMemory, 0x7ffff000, stackMemory.size());
        mem.add(stack);
        long sp = 0x7ffffff0;

        // setup posix environment and create executable at /tmp/test
        PosixEnvironment env = new PosixEnvironment(mem, "x86_64");
        FileSystem tmpfs = new Tmpfs(env.getVFS());
        Posix posix = env.getPosix();
        posix.mkdir("/tmp", 0755);
        env.mount("/tmp", tmpfs);
        posix.chdir("/tmp");
        int fd = posix.open("./test", Fcntl.O_CREAT | Fcntl.O_WRONLY, 0755);
        BytePosixPointer ptr = new BytePosixPointer(data);
        posix.write(fd, ptr, data.length);
        posix.close(fd);

        // load ./test with arguments "hello" and "world"
        ElfLoader loader = new ElfLoader();
        Map<String, String> environ = new HashMap<>();
        environ.put("PATH", "/bin:/usr/bin");
        loader.setArguments("./test", "hello", "world");
        loader.setEnvironment(environ);
        loader.setProgramName("./test");
        loader.setVirtualMemory(mem);
        loader.setSP(sp);
        loader.setPosixEnvironment(env);

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
    }
}
