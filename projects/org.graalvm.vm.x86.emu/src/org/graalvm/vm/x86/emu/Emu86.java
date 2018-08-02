package org.graalvm.vm.x86.emu;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.graalvm.vm.memory.ByteMemory;
import org.graalvm.vm.memory.Memory;
import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.memory.hardware.linux.MemoryMap;
import org.graalvm.vm.memory.hardware.linux.MemorySegment;
import org.graalvm.vm.x86.ElfLoader;
import org.graalvm.vm.x86.posix.PosixEnvironment;

import com.everyware.posix.api.PosixException;
import com.everyware.posix.vfs.FileSystem;
import com.everyware.posix.vfs.NativeFileSystem;
import com.everyware.posix.vfs.VFS;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;

public class Emu86 {
    public static final long STACK_SIZE = 8 * 1024 * 1024; // 8M
    public static final long STACK_ADDRESS = 0x7fff6c845000L;
    // public static final long STACK_ADDRESS = 0xf6fff000L;
    // public static final long STACK_ADDRESS = 0x0000800000000000L;
    public static final long STACK_BASE = STACK_ADDRESS - STACK_SIZE;

    private static void run(Ptrace ptrace, String[] args) throws PosixException, IOException {
        VirtualMemory mem = new PtraceVirtualMemory(ptrace);
        PosixEnvironment posix = new PosixEnvironment(mem, "x86_64");

        // long addr = ptrace.mmap(0, 4096, true, true, false, false, true, false, -1, 0);
        // ptrace.write(addr, 0x0A46454542L); // "BEEF\n"
        // ptrace.syscall(1, 1, addr, 5, 0, 0, 0);
        // ptrace.munmap(addr, 4096);

        ElfLoader loader = new ElfLoader();
        loader.setPosixEnvironment(posix);
        loader.setVirtualMemory(mem);
        loader.setEnvironment(System.getenv());
        loader.setArguments(args);

        long stackbase = mem.pageStart(STACK_BASE);
        long stacksize = mem.roundToPageSize(STACK_SIZE);
        Memory stackMemory = new ByteMemory(stacksize, false);
        MemoryPage stack = new MemoryPage(stackMemory, stackbase, stacksize, "[stack]");
        mem.add(stack);
        long sp = STACK_ADDRESS - 16;
        assert (sp & 0xf) == 0;

        loader.setSP(sp);

        // loading binary...
        VFS vfs = posix.getVFS();
        Path cwd = Paths.get(".").toAbsolutePath().normalize();
        FileSystem fs;

        String fsroot = System.getProperty("vm.power.fsroot");
        if (fsroot != null) {
            fs = new NativeFileSystem(vfs, fsroot);
            String cwdprop = System.getProperty("vm.power.cwd");
            if (cwdprop != null) {
                cwd = Paths.get(cwdprop);
            } else {
                cwd = Paths.get("/");
            }
        } else {
            fs = new NativeFileSystem(vfs, cwd.getRoot().toString());
        }

        posix.mount("/", fs);
        StringBuilder posixPath = new StringBuilder();
        if (cwd.getNameCount() == 0) {
            posixPath.append('/');
        }
        for (int i = 0; i < cwd.getNameCount(); i++) {
            posixPath.append('/').append(cwd.getName(i));
        }
        posix.getPosix().chdir(posixPath.toString());

        String execfn = vfs.resolve(args[0]); // get absolute path
        posix.setExecfn(execfn);

        loader.setProgramName(execfn);
        loader.load(execfn);

        Registers regs = ptrace.getRegisters();
        regs.rax = 0;
        regs.rbx = 0;
        regs.rcx = 0;
        regs.rdx = 0;
        regs.rsi = 0;
        regs.rdi = 0;
        regs.rbp = 0;
        regs.rsp = loader.getSP();
        regs.r8 = 0;
        regs.r9 = 0;
        regs.r10 = 0;
        regs.r11 = 0;
        regs.r12 = 0;
        regs.r13 = 0;
        regs.r14 = 0;
        regs.r15 = 0;
        regs.fs_base = 0;
        regs.gs_base = 0;
        regs.rflags = 0;
        regs.rip = loader.getPC();
        for (int i = 0; i < regs.xmm_space.length; i++) {
            regs.xmm_space[i] = 0;
        }
        ptrace.setRegisters(regs);

        Interpreter interp = new Interpreter(ptrace, posix, mem, loader.getSymbols());
        int code = interp.execute();
        System.exit(code);
    }

    public static void main(String[] args) throws PosixException, IOException {
        Trace.setupConsoleApplication(Levels.INFO);
        System.loadLibrary("emu86");

        if (args.length == 0) {
            System.out.println("Usage: emu86 program [args...]");
            System.exit(1);
        }

        int pid = -1;
        try (Ptrace ptrace = new Ptrace()) {
            pid = ptrace.getPid();

            try {
                run(ptrace, args);
            } catch (SegmentationViolation e) {
                e.printStackTrace();
                Registers regs = ptrace.getRegisters();
                System.out.println("Register dump:");
                System.out.println(regs);
                System.out.println("Memory map for process " + pid + ":");
                MemoryMap map = new MemoryMap(pid);
                for (MemorySegment s : map.getSegments()) {
                    System.out.println(s);
                }
            }
        }
    }
}
