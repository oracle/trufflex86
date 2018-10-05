package org.graalvm.vm.memory.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixException;
import com.everyware.util.UnsafeHolder;
import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import sun.misc.Unsafe;

public class MMU {
    private static final Logger log = Trace.create(MMU.class);

    @CompilationFinal private static boolean initialized = false;
    private static final Unsafe unsafe = UnsafeHolder.getUnsafe();
    @CompilationFinal private static long ptr;

    public static boolean init(long lo, long hi) {
        CompilerDirectives.transferToInterpreter();
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }
        try {
            loadLibrary();
            ptr = setup(lo, hi);
            if (ptr == 0) {
                log.log(Level.INFO, "cannot setup VM handler");
                return false;
            }
            initialized = true;
            try {
                mmap(lo, hi - lo, false, false, false, true, true, false, -1, 0);
            } catch (PosixException e) {
                log.log(Level.INFO, "cannot pre-allocate VM region");
            }
            return true;
        } catch (UnsatisfiedLinkError e) {
            log.log(Level.INFO, "cannot load libmemory");
            return false;
        } catch (PosixException e) {
            log.log(Levels.ERROR, "Error: " + Errno.toString(e.getErrno()));
            return false;
        }
    }

    public static long getSegfaultAddress() {
        if (!initialized) {
            CompilerDirectives.transferToInterpreter();
            throw new IllegalStateException("native code not initialized");
        }
        long val = unsafe.getLong(ptr);
        if (val != 0) {
            unsafe.putLong(ptr, 0);
        }
        return val;
    }

    private static void loadLibrary() throws UnsatisfiedLinkError {
        System.loadLibrary("memory");
    }

    private static native long setup(long lo, long hi) throws PosixException;

    public static native long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException;

    public static native int munmap(long addr, long len) throws PosixException;

    public static native int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException;
}
