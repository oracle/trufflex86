package org.graalvm.vm.x86.posix;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.node.AMD64Node;

import com.everyware.posix.api.Errno;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class SyscallWrapper extends AMD64Node {
    private static final Logger log = Trace.create(SyscallWrapper.class);

    public static final int SYS_read = 0;
    public static final int SYS_write = 1;
    public static final int SYS_open = 2;
    public static final int SYS_close = 3;
    public static final int SYS_stat = 4;
    public static final int SYS_fstat = 5;
    public static final int SYS_lstat = 6;
    public static final int SYS_lseek = 8;
    public static final int SYS_mmap = 9;
    public static final int SYS_mprotect = 10;
    public static final int SYS_munmap = 11;
    public static final int SYS_brk = 12;
    public static final int SYS_rt_sigprocmask = 14;
    public static final int SYS_ioctl = 16;
    public static final int SYS_readv = 19;
    public static final int SYS_writev = 20;
    public static final int SYS_access = 21;
    public static final int SYS_getpid = 39;
    public static final int SYS_exit = 60;
    public static final int SYS_uname = 63;
    public static final int SYS_fcntl = 72;
    public static final int SYS_getdents = 78;
    public static final int SYS_getcwd = 79;
    public static final int SYS_readlink = 89;
    public static final int SYS_gettimeofday = 96;
    public static final int SYS_getuid = 102;
    public static final int SYS_getgid = 104;
    public static final int SYS_arch_prctl = 158;
    public static final int SYS_exit_group = 231;
    public static final int SYS_tgkill = 234;
    public static final int SYS_openat = 257;

    public static final int SYS_DEBUG = 0xDEADBEEF;
    public static final int SYS_PRINTK = 0xDEADBABE;

    private final PosixEnvironment posix;
    private final VirtualMemory memory;

    @Child private ArchPrctl prctl;

    public SyscallWrapper(PosixEnvironment posix, VirtualMemory memory) {
        this.posix = posix;
        this.memory = memory;
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

    public long executeI64(VirtualFrame frame, int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7) throws SyscallException {
        switch (nr) {
            case SYS_arch_prctl:
                if (prctl == null) {
                    CompilerDirectives.transferToInterpreter();
                    prctl = insert(new ArchPrctl());
                }
                return prctl.execute(frame, (int) a1, a2);
        }
        return executeWrapper(nr, a1, a2, a3, a4, a5, a6, a7);
    }

    @TruffleBoundary
    private long executeWrapper(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7) throws SyscallException {
        log.log(Levels.DEBUG, () -> String.format("syscall %d: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", nr, a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6, a7, a7));
        switch (nr) {
            case SYS_read:
                return posix.read((int) a1, a2, a3);
            case SYS_write:
                return posix.write((int) a1, a2, a3);
            case SYS_open:
                return posix.open(a1, (int) a2, (int) a3);
            case SYS_close:
                return posix.close((int) a1);
            case SYS_stat:
                return posix.stat(a1, a2);
            case SYS_fstat:
                return posix.fstat((int) a1, a2);
            case SYS_lstat:
                return posix.lstat(a1, a2);
            case SYS_lseek:
                return posix.lseek((int) a1, a2, (int) a3);
            case SYS_mmap:
                return posix.mmap(a1, a2, (int) a3, (int) a4, (int) a5, a6);
            case SYS_mprotect:
                return posix.mprotect(a1, a2, (int) a3);
            case SYS_munmap:
                return posix.munmap(a1, a2);
            case SYS_brk:
                return brk(a1);
            case SYS_rt_sigprocmask:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("rt_sigprocmask(%s, ..., %s)", a1, a4));
                }
                throw new SyscallException(Errno.ENOSYS);
            case SYS_ioctl:
                return posix.ioctl((int) a1, a2, a3);
            case SYS_readv:
                return posix.readv((int) a1, a2, (int) a3);
            case SYS_writev:
                return posix.writev((int) a1, a2, (int) a3);
            case SYS_access:
                return posix.access(a1, (int) a2);
            case SYS_getpid:
                return posix.getpid();
            case SYS_exit:
            case SYS_exit_group: // TODO: implement difference
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("exit(%d)", (int) a1));
                }
                throw new ProcessExitException((int) a1);
            case SYS_uname:
                return posix.uname(a1);
            case SYS_fcntl:
                return posix.fcntl((int) a1, (int) a2, a3);
            case SYS_getdents:
                return posix.getdents((int) a1, a2, (int) a3);
            case SYS_getcwd:
                return posix.getcwd(a1, a2);
            case SYS_readlink:
                return posix.readlink(a1, a2, a3);
            case SYS_gettimeofday:
                return posix.gettimeofday(a1, a2);
            case SYS_getuid:
                return posix.getuid();
            case SYS_getgid:
                return posix.getgid();
            case SYS_tgkill:
                if (posix.isStrace()) {
                    log.log(Level.INFO, () -> String.format("tgkill(%d, %d, %d)", (int) a1, (int) a2, (int) a3));
                }
                throw new ProcessExitException(128 + (int) a3);
            case SYS_openat:
                return posix.openat((int) a1, a2, (int) a3, (int) a4);
            case SYS_DEBUG:
                log.log(Levels.INFO, String.format("DEBUG: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6, a7, a7));
                return 0;
            case SYS_PRINTK:
                if (posix.isStrace()) {
                    log.log(Level.INFO, "printk(...)");
                }
                posix.printk(a1, a2, a3, a4, a5, a6, a7);
                return 0;
            default:
                throw new SyscallException(Errno.ENOSYS);
        }
    }
}
