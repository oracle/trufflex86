package org.graalvm.vm.x86.posix;

import java.util.logging.Logger;

import org.graalvm.vm.x86.node.AMD64Node;

import com.everyware.posix.api.Errno;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class SyscallWrapper extends AMD64Node {
    private static final Logger log = Trace.create(SyscallWrapper.class);

    public static final int SYS_read = 0;
    public static final int SYS_write = 1;
    public static final int SYS_open = 2;
    public static final int SYS_close = 3;
    public static final int SYS_exit = 60;
    public static final int SYS_exit_group = 231;

    private final PosixEnvironment posix;

    public SyscallWrapper(PosixEnvironment posix) {
        this.posix = posix;
    }

    @TruffleBoundary
    public long executeI64(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7) throws SyscallException {
        log.log(Levels.INFO, () -> String.format("syscall %d: %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x), %d (%x)", nr, a1, a1, a2, a2, a3, a3, a4, a4, a5, a5, a6, a6, a7, a7));
        switch (nr) {
            case SYS_read:
                return posix.read((int) a1, a2, a3);
            case SYS_write:
                return posix.write((int) a1, a2, a3);
            case SYS_open:
                return posix.open(a1, (int) a2, (int) a3);
            case SYS_close:
                return posix.close((int) a1);
            case SYS_exit:
            case SYS_exit_group: // TODO: implement difference
                throw new ProcessExitException((int) a1);
            default:
                throw new SyscallException(Errno.ENOSYS);
        }
    }
}
