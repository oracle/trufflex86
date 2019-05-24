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
package org.graalvm.vm.x86.emu;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.elf.Symbol;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.SymbolResolver;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeMemoryReader;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.CpuidBits;
import org.graalvm.vm.x86.isa.instruction.Cpuid;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceWriter;
import org.graalvm.vm.x86.posix.ArchPrctl;
import org.graalvm.vm.x86.posix.PosixEnvironment;
import org.graalvm.vm.x86.posix.ProcessExitException;
import org.graalvm.vm.x86.posix.SyscallException;
import org.graalvm.vm.x86.posix.SyscallNames;
import org.graalvm.vm.x86.posix.Syscalls;

public class Interpreter {
    private static final Logger log = Trace.create(Interpreter.class);

    private static final boolean TRACE = Options.getBoolean(Options.DEBUG_EXEC);
    private static final boolean useInstructionCount = Options.getBoolean(Options.RDTSC_USE_INSTRUCTION_COUNT);

    private static final boolean BINARY_TRACE = Options.getBoolean(Options.DEBUG_EXEC_TRACE);

    private static final long SYSCALL = 0x050F;
    private static final long CPUID = 0xA20F;
    private static final long RDTSC = 0x310F;
    private static final long REP_STOSB = 0xAAF3;
    private static final long REP_STOSD = 0xABF3;
    private static final long REP_STOSQ = 0xAB48F3;

    private final Ptrace ptrace;
    private Registers regs;
    private final PosixEnvironment posix;
    private final PtraceVirtualMemory memory;

    private SymbolResolver symbolResolver;

    private final ExecutionTraceWriter trace;

    private long insncnt;

    public Interpreter(Ptrace ptrace, PosixEnvironment posix, PtraceVirtualMemory memory, NavigableMap<Long, Symbol> symbols, ExecutionTraceWriter trace) throws PosixException {
        this.ptrace = ptrace;
        this.posix = posix;
        this.memory = memory;
        this.trace = trace;
        regs = ptrace.getRegisters();
        symbolResolver = new SymbolResolver(symbols);
        insncnt = 0;
    }

    public void close() throws IOException {
        if (trace != null) {
            trace.close();
        }
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
            case Syscalls.SYS_poll:
                return posix.poll(a1, (int) a2, (int) a3);
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
                return posix.rt_sigprocmask((int) a1, a2, a3, (int) a4);
            case Syscalls.SYS_ioctl:
                return posix.ioctl((int) a1, a2, a3);
            case Syscalls.SYS_pread64:
                return posix.pread64((int) a1, a2, (int) a3, a4);
            case Syscalls.SYS_pwrite64:
                return posix.pwrite64((int) a1, a2, (int) a3, a4);
            case Syscalls.SYS_readv:
                return posix.readv((int) a1, a2, (int) a3);
            case Syscalls.SYS_writev:
                return posix.writev((int) a1, a2, (int) a3);
            case Syscalls.SYS_access:
                return posix.access(a1, (int) a2);
            case Syscalls.SYS_dup:
                return posix.dup((int) a1);
            case Syscalls.SYS_dup2:
                return posix.dup2((int) a1, (int) a2);
            case Syscalls.SYS_getpid:
                return posix.getpid();
            case Syscalls.SYS_socket:
                return posix.socket((int) a1, (int) a2, (int) a3);
            case Syscalls.SYS_connect:
                return posix.connect((int) a1, a2, (int) a3);
            case Syscalls.SYS_sendto:
                return posix.sendto((int) a1, a2, a3, (int) a4, a5, (int) a6);
            case Syscalls.SYS_recvfrom:
                return posix.recvfrom((int) a1, a2, a3, (int) a4, a5, a6);
            case Syscalls.SYS_recvmsg:
                return posix.recvmsg((int) a1, a2, (int) a3);
            case Syscalls.SYS_shutdown:
                return posix.shutdown((int) a1, (int) a2);
            case Syscalls.SYS_bind:
                return posix.bind((int) a1, a2, (int) a3);
            case Syscalls.SYS_getsockname:
                return posix.getsockname((int) a1, a2, a3);
            case Syscalls.SYS_getpeername:
                return posix.getpeername((int) a1, a2, a3);
            case Syscalls.SYS_setsockopt:
                return posix.setsockopt((int) a1, (int) a2, (int) a3, a4, (int) a5);
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
            case Syscalls.SYS_creat:
                return posix.creat(a1, (int) a2);
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
            case Syscalls.SYS_sigaltstack:
                return posix.sigaltstack(a1, a2);
            case Syscalls.SYS_arch_prctl:
                return arch_prctl((int) a1, a2);
            case Syscalls.SYS_gettid:
                return posix.gettid();
            case Syscalls.SYS_time:
                return posix.time(a1);
            case Syscalls.SYS_futex:
                return posix.futex(a1, (int) a2, (int) a3, a4, a5, (int) a6);
            case Syscalls.SYS_getdents64:
                return posix.getdents64((int) a1, a2, (int) a3);
            case Syscalls.SYS_clock_gettime:
                return posix.clock_gettime((int) a1, a2);
            case Syscalls.SYS_clock_getres:
                return posix.clock_getres((int) a1, a2);
            case Syscalls.SYS_tgkill:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("tgkill(%d, %d, %d)", (int) a1, (int) a2, (int) a3));
                }
                throw new ProcessExitException(128 + (int) a3);
            case Syscalls.SYS_openat:
                return posix.openat((int) a1, a2, (int) a3, (int) a4);
            case Syscalls.SYS_dup3:
                return posix.dup3((int) a1, (int) a2, (int) a3);
            case Syscalls.SYS_prlimit64:
                return posix.prlimit64((int) a1, (int) a2, a3, a4);
            case Syscalls.SYS_DEBUG:
                log.log(Level.INFO, String.format("DEBUG: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6));
                return 0;
            case Syscalls.SYS_PRINTK:
                if (posix.isStrace()) {
                    log.log(Level.INFO, "printk(...)");
                }
                posix.printk(a1, a2, a3, a4, a5, a6);
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
                a = Cpuid.PROCESSOR_INFO;
                b = Cpuid.BRAND_INDEX | (Cpuid.CLFLUSH_LINE_SIZE << 8);
                c = CpuidBits.SSE3 | CpuidBits.SSE41 | CpuidBits.SSE42 | CpuidBits.POPCNT | CpuidBits.RDRND;
                d = CpuidBits.TSC | CpuidBits.CMOV | CpuidBits.CLFSH | CpuidBits.FXSR | CpuidBits.SSE | CpuidBits.SSE2;
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
        if (TRACE || BINARY_TRACE) {
            boolean wasDebug = memory.getDebug();
            memory.setDebug(false);
            Symbol sym = symbolResolver.getSymbol(regs.rip);
            String func = sym == null ? "" : sym.getName();
            CodeReader reader = new CodeMemoryReader(memory, regs.rip);
            AMD64Instruction insn = AMD64InstructionDecoder.decode(regs.rip, reader);
            if (trace == null) {
                System.out.println("----------------\nIN: " + func);
                System.out.println("0x" + HexFormatter.tohex(regs.rip, 8) + ":\t" + insn + "\n");
                System.out.println(regs);
            } else {
                CpuState state = regs.toCpuState();
                trace.step(state, null, func, 0, insn);
            }
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
        } else if ((insn & 0xFFFF) == REP_STOSB || ((insn & 0xFFFF) == REP_STOSD) || ((insn & 0xFFFFFF) == REP_STOSQ)) {
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
