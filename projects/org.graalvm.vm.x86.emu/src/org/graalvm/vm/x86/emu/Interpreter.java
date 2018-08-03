package org.graalvm.vm.x86.emu;

import java.util.NavigableMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.util.HexFormatter;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.CpuidBits;
import org.graalvm.vm.x86.isa.instruction.Cpuid;
import org.graalvm.vm.x86.posix.ArchPrctl;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.ProcessExitException;
import org.graalvm.vm.x86.posix.SyscallException;
import org.graalvm.vm.x86.posix.SyscallNames;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixException;
import com.everyware.posix.elf.Symbol;
import com.everyware.util.log.Trace;

public class Interpreter {
    private static final Logger log = Trace.create(Interpreter.class);

    private static final boolean TRACE = Options.getBoolean(Options.DEBUG_EXEC);
    private static final boolean useInstructionCount = Options.getBoolean(Options.RDTSC_USE_INSTRUCTION_COUNT);

    private static final long SYSCALL = 0x050F;
    private static final long CPUID = 0xA20F;
    private static final long RDTSC = 0x310F;
    private static final long REP_STOSB = 0xAAF3;
    private static final long REP_STOSQ = 0xAB48F3;

    private Ptrace ptrace;
    private Registers regs;
    private PosixEnvironment posix;
    private PtraceVirtualMemory memory;

    private SymbolResolver symbolResolver;

    private long insncnt;

    public Interpreter(Ptrace ptrace, PosixEnvironment posix, PtraceVirtualMemory memory, NavigableMap<Long, Symbol> symbols) throws PosixException {
        this.ptrace = ptrace;
        this.posix = posix;
        this.memory = memory;
        regs = ptrace.getRegisters();
        symbolResolver = new SymbolResolver(symbols);
        insncnt = 0;
    }

    private long brk(long addr) {
        if (addr == 0) {
            long brk = memory.brk();
            if (posix.isStrace()) {
                log.log(Level.INFO, () -> String.format("brk(NULL) = 0x%016x", brk));
            }
            return brk;
        } else {
            long newbrk = addr;
            long brk = memory.brk(newbrk);
            if (posix.isStrace()) {
                log.log(Level.INFO, () -> String.format("brk(0x%016x) = 0x%016x", newbrk, brk));
            }
            return brk;
        }
    }

    private long arch_prctl(int code, long addr) throws SyscallException {
        if (posix.isStrace()) {
            tracePrctl(code, addr);
        }
        switch (code) {
            case ArchPrctl.ARCH_GET_FS:
                memory.setI64(addr, regs.fs_base);
                break;
            case ArchPrctl.ARCH_GET_GS:
                memory.setI64(addr, regs.gs_base);
                break;
            case ArchPrctl.ARCH_SET_FS:
                regs.fs_base = addr;
                break;
            case ArchPrctl.ARCH_SET_GS:
                regs.gs_base = addr;
                break;
            default:
                System.out.printf("arch_prctl(0x%x): invalid code\n", code);
                throw new SyscallException(Errno.EINVAL);
        }
        return 0;
    }

    private static void tracePrctl(int code, long addr) {
        String name;
        switch (code) {
            case ArchPrctl.ARCH_GET_FS:
                name = "ARCH_GET_FS";
                break;
            case ArchPrctl.ARCH_GET_GS:
                name = "ARCH_GET_GS";
                break;
            case ArchPrctl.ARCH_SET_FS:
                name = "ARCH_SET_FS";
                break;
            case ArchPrctl.ARCH_SET_GS:
                name = "ARCH_SET_GS";
                break;
            default:
                name = Integer.toString(code);
        }
        log.log(Level.INFO, () -> String.format("arch_prctl(%s, 0x%x)", name, addr));
    }

    private static void logENOSYS(long nr) {
        String name = SyscallNames.getName(nr);
        if (name != null) {
            log.log(Level.WARNING, "Unsupported syscall " + name + " (#" + nr + ")");
        } else {
            log.log(Level.WARNING, "Unsupported syscall " + nr);
        }
    }

    private long syscall(int nr, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException, ProcessExitException {
        switch (nr) {
            case Syscalls.SYS_read:
                return posix.read((int) a1, a2, a3);
            case Syscalls.SYS_write:
                return posix.write((int) a1, a2, a3);
            case Syscalls.SYS_open:
                return posix.open(a1, (int) a2, (int) a3);
            case Syscalls.SYS_close:
                return posix.close((int) a1);
            case Syscalls.SYS_stat:
                return posix.stat(a1, a2);
            case Syscalls.SYS_fstat:
                return posix.fstat((int) a1, a2);
            case Syscalls.SYS_lstat:
                return posix.lstat(a1, a2);
            case Syscalls.SYS_lseek:
                return posix.lseek((int) a1, a2, (int) a3);
            case Syscalls.SYS_mmap:
                return posix.mmap(a1, a2, (int) a3, (int) a4, (int) a5, a6);
            case Syscalls.SYS_mprotect:
                return posix.mprotect(a1, a2, (int) a3);
            case Syscalls.SYS_munmap:
                return posix.munmap(a1, a2);
            case Syscalls.SYS_brk:
                return brk(a1);
            case Syscalls.SYS_rt_sigaction:
                return posix.rt_sigaction((int) a1, a2, a3);
            case Syscalls.SYS_rt_sigprocmask:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("rt_sigprocmask(%s, ..., %s)", a1, a4));
                }
                throw new SyscallException(Errno.ENOSYS);
            case Syscalls.SYS_ioctl:
                return posix.ioctl((int) a1, a2, a3);
            case Syscalls.SYS_readv:
                return posix.readv((int) a1, a2, (int) a3);
            case Syscalls.SYS_writev:
                return posix.writev((int) a1, a2, (int) a3);
            case Syscalls.SYS_access:
                return posix.access(a1, (int) a2);
            case Syscalls.SYS_getpid:
                return posix.getpid();
            case Syscalls.SYS_exit:
            case Syscalls.SYS_exit_group: // TODO: implement difference
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("exit(%d)", (int) a1));
                }
                throw new ProcessExitException((int) a1);
            case Syscalls.SYS_uname:
                return posix.uname(a1);
            case Syscalls.SYS_fcntl:
                return posix.fcntl((int) a1, (int) a2, a3);
            case Syscalls.SYS_fsync:
                return posix.fsync((int) a1);
            case Syscalls.SYS_getdents:
                return posix.getdents((int) a1, a2, (int) a3);
            case Syscalls.SYS_getcwd:
                return posix.getcwd(a1, a2);
            case Syscalls.SYS_unlink:
                return posix.unlink(a1);
            case Syscalls.SYS_readlink:
                return posix.readlink(a1, a2, a3);
            case Syscalls.SYS_gettimeofday:
                return posix.gettimeofday(a1, a2);
            case Syscalls.SYS_sysinfo:
                return posix.sysinfo(a1);
            case Syscalls.SYS_times:
                return posix.times(a1);
            case Syscalls.SYS_getuid:
                return posix.getuid();
            case Syscalls.SYS_getgid:
                return posix.getgid();
            case Syscalls.SYS_setuid:
                return posix.setuid(a1);
            case Syscalls.SYS_setgid:
                return posix.setgid(a1);
            case Syscalls.SYS_geteuid:
                return posix.geteuid();
            case Syscalls.SYS_getegid:
                return posix.getegid();
            case Syscalls.SYS_arch_prctl:
                return arch_prctl((int) a1, a2);
            case Syscalls.SYS_gettid:
                return posix.gettid();
            case Syscalls.SYS_time:
                return posix.time(a1);
            case Syscalls.SYS_clock_gettime:
                return posix.clock_gettime((int) a1, a2);
            case Syscalls.SYS_tgkill:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("tgkill(%d, %d, %d)", (int) a1, (int) a2, (int) a3));
                }
                throw new ProcessExitException(128 + (int) a3);
            case Syscalls.SYS_openat:
                return posix.openat((int) a1, a2, (int) a3, (int) a4);
            case Syscalls.SYS_prlimit64:
                return posix.prlimit64((int) a1, (int) a2, a3, a4);
            case Syscalls.SYS_DEBUG:
                log.log(Level.INFO, String.format("DEBUG: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6));
                return 0;
            case Syscalls.SYS_PRINTK:
                if (posix.isStrace()) {
                    log.log(Level.INFO, "printk(...)");
                }
                posix.printk(a1, a2, a3, a4, a5, a6, 0);
                return 0;
            default:
                logENOSYS(nr);
                throw new SyscallException(Errno.ENOSYS);
        }
    }

    private void cpuid() {
        int level = (int) regs.rax;

        int a;
        int b;
        int c;
        int d;

        switch (level) {
            case 0:
                // Get Vendor ID/Highest Function Parameter
                a = 7; // max supported function
                b = Cpuid.VENDOR_ID_I32[0];
                d = Cpuid.VENDOR_ID_I32[1];
                c = Cpuid.VENDOR_ID_I32[2];
                break;
            case 1:
                // Processor Info and Feature Bits
                // EAX:
                // 3:0 - Stepping
                // 7:4 - Model
                // 11:8 - Family
                // 13:12 - Processor Type
                // 19:16 - Extended Model
                // 27:20 - Extended Family
                a = 0;
                b = 0;
                c = CpuidBits.SSE3 | CpuidBits.SSE41 | CpuidBits.SSE42 | CpuidBits.POPCNT | CpuidBits.RDRND;
                d = CpuidBits.TSC | CpuidBits.CMOV | CpuidBits.FXSR | CpuidBits.SSE | CpuidBits.SSE2;
                break;
            case 7:
                // Extended Features (FIXME: assumption is ECX=0)
                a = 0;
                b = CpuidBits.RDSEED;
                c = 0;
                d = 0;
                break;
            case 0x80000000:
                // Get Highest Extended Function Supported
                a = 0x80000004;
                b = 0;
                c = 0;
                d = 0;
                break;
            case 0x80000001:
                // Extended Processor Info and Feature Bits
                a = 0;
                b = 0;
                c = CpuidBits.LAHF;
                d = CpuidBits.LM;
                break;
            case 0x80000002:
                // Processor Brand String
                a = Cpuid.BRAND_I32[0];
                b = Cpuid.BRAND_I32[1];
                c = Cpuid.BRAND_I32[2];
                d = Cpuid.BRAND_I32[3];
                break;
            case 0x80000003:
                // Processor Brand String
                a = Cpuid.BRAND_I32[4];
                b = Cpuid.BRAND_I32[5];
                c = Cpuid.BRAND_I32[6];
                d = Cpuid.BRAND_I32[7];
                break;
            case 0x80000004:
                // Processor Brand String
                a = Cpuid.BRAND_I32[8];
                b = Cpuid.BRAND_I32[9];
                c = Cpuid.BRAND_I32[10];
                d = Cpuid.BRAND_I32[11];
                break;
            default:
                // Fallback: bits cleared = feature(s) not available
                a = 0;
                b = 0;
                c = 0;
                d = 0;
        }
        regs.rax = Integer.toUnsignedLong(a);
        regs.rbx = Integer.toUnsignedLong(b);
        regs.rcx = Integer.toUnsignedLong(c);
        regs.rdx = Integer.toUnsignedLong(d);
    }

    public void step() throws ProcessExitException, PosixException {
        regs = ptrace.getRegisters();
        if (TRACE) {
            boolean wasDebug = memory.getDebug();
            memory.setDebug(false);
            Symbol sym = symbolResolver.getSymbol(regs.rip);
            String func = sym == null ? "" : sym.getName();
            CodeReader reader = new CodeMemoryReader(memory, regs.rip);
            String insn = AMD64InstructionDecoder.decode(regs.rip, reader).getDisassembly();
            System.out.println("----------------\nIN: " + func);
            System.out.println("0x" + HexFormatter.tohex(regs.rip, 8) + ":\t" + insn + "\n");
            System.out.println(regs);
            memory.setDebug(wasDebug);
        }
        long insn = ptrace.read(regs.rip);
        if ((insn & 0xFFFF) == SYSCALL) {
            try {
                regs.rax = syscall((int) regs.rax, regs.rdi, regs.rsi, regs.rdx, regs.r10, regs.r8, regs.r9);
            } catch (SyscallException e) {
                regs.rax = -e.getValue();
            }
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if ((insn & 0xFFFF) == CPUID) {
            cpuid();
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if (useInstructionCount && (insn & 0xFFFF) == RDTSC) {
            long time = insncnt;
            int high = (int) (time >> 32);
            int low = (int) time;
            regs.rax = Integer.toUnsignedLong(low);
            regs.rdx = Integer.toUnsignedLong(high);
            regs.rip += 2;
            ptrace.setRegisters(regs);
        } else if ((insn & 0xFFFF) == REP_STOSB || ((insn & 0xFFFFFF) == REP_STOSQ)) {
            // step next
            long rip = regs.rip;
            do {
                if (!ptrace.step()) {
                    throw new ProcessExitException(-1);
                }
                insncnt++;
                regs = ptrace.getRegisters();
            } while (regs.rip == rip);
            insncnt--;
        } else {
            if (!ptrace.step()) {
                throw new ProcessExitException(-1);
            }
        }
        insncnt++;
    }

    public int execute() throws PosixException {
        try {
            while (true) {
                step();
            }
        } catch (ProcessExitException e) {
            return e.getCode();
        }
    }
}
