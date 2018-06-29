package org.graalvm.vm.memory.test.hardware;

import static org.graalvm.vm.memory.hardware.MMU.mmap;
import static org.graalvm.vm.memory.hardware.MMU.munmap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.hardware.NativeMemory;
import org.graalvm.vm.memory.hardware.NativeVirtualMemory;
import org.graalvm.vm.memory.hardware.MMU;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.everyware.posix.api.PosixException;

public class MMUTest {
    private NativeVirtualMemory mem;

    @BeforeClass
    public static void init() {
        try {
            TestOptions.setLibraryPath();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        assertTrue(MMU.init(NativeVirtualMemory.LOW, NativeVirtualMemory.HIGH));
    }

    @Before
    public void setup() {
        mem = new NativeVirtualMemory(NativeVirtualMemory.LOW, NativeVirtualMemory.HIGH, 0, NativeVirtualMemory.SIZE);
    }

    @Test
    public void testMmap1() throws PosixException {
        long ptr = mmap(0, 4096, true, true, false, false, true, false, -1, 0);
        assertTrue(ptr != 0);
        munmap(ptr, 4096);
    }

    @Test
    public void testMemoryAccess1() throws PosixException {
        long ptr = mmap(0, 4096, true, true, false, false, true, false, -1, 0);
        assertTrue(ptr != 0);
        try {
            NativeMemory.i8(ptr + 0, (byte) 'B');
            NativeMemory.i8(ptr + 1, (byte) 'E');
            NativeMemory.i8(ptr + 2, (byte) 'E');
            NativeMemory.i8(ptr + 3, (byte) 'F');
            int val = NativeMemory.i32B(ptr);
            assertEquals(0x42454546, val);
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void vm01() throws PosixException {
        long ptr = mmap(mem.getPhysicalLow(), 4096, true, true, false, true, true, false, -1, 0);
        try {
            mem.i8(0);
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void vmSegfault01() throws PosixException {
        long ptr = mmap(mem.getPhysicalLow(), 4096, true, true, false, true, true, false, -1, 0);
        try {
            mem.i8(4097);
            fail();
        } catch (SegmentationViolation e) {
            assertEquals(4097, e.getAddress());
        } finally {
            munmap(ptr, 4096);
        }
    }

    @Test
    public void segfault() {
        NativeMemory.i8(NativeVirtualMemory.LOW);
    }
}
