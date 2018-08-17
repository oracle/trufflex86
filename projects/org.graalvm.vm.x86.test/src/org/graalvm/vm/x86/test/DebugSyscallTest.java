package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.graalvm.vm.memory.JavaVirtualMemory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.SyscallException;
import org.junit.Before;
import org.junit.Test;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixPointer;

public class DebugSyscallTest {
    private PosixEnvironment posix;
    private VirtualMemory vm;
    private ByteArrayOutputStream stdout;

    @Before
    public void setup() {
        stdout = new ByteArrayOutputStream();
        vm = new JavaVirtualMemory();
        posix = new PosixEnvironment(vm, "x86_64");
        posix.setStandardOut(stdout);
    }

    private String stdout() {
        return stdout.toString();
    }

    private long map(String str) {
        byte[] bytes = new byte[(int) vm.roundToPageSize(str.length() + 1)];
        MemoryPage page = vm.allocate(bytes.length);
        PosixPointer dst = vm.getPosixPointer(page.getBase());
        CString.strcpy(dst, str);
        return page.getBase();
    }

    @Test
    public void printkPlain() throws SyscallException {
        long fmt = map("Hello world!\n");
        posix.printk(fmt, 0, 0, 0, 0, 0, 0);
        assertEquals("Hello world!\n", stdout());
    }

    @Test
    public void printkString() throws SyscallException {
        long fmt = map("Hello %s!\n");
        long str = map("world");
        posix.printk(fmt, str, 0, 0, 0, 0, 0);
        assertEquals("Hello world!\n", stdout());
    }

    @Test
    public void printkDecimal() throws SyscallException {
        long fmt = map("Hello %d!\n");
        posix.printk(fmt, 42, 0, 0, 0, 0, 0);
        assertEquals("Hello 42!\n", stdout());
    }

    @Test
    public void printkTwoDecimal() throws SyscallException {
        long fmt = map("Hello %d:%d!\n");
        posix.printk(fmt, 42, 21, 0, 0, 0, 0);
        assertEquals("Hello 42:21!\n", stdout());
    }

    @Test
    public void printkThreeDecimal() throws SyscallException {
        long fmt = map("Hello %d:%d:%d!\n");
        posix.printk(fmt, 42, 21, 13, 0, 0, 0);
        assertEquals("Hello 42:21:13!\n", stdout());
    }
}
