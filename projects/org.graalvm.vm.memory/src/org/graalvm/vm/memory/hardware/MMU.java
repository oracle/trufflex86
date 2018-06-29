package org.graalvm.vm.memory.hardware;

import com.everyware.posix.api.Errno;
import com.everyware.posix.api.PosixException;
import com.everyware.util.UnsafeHolder;

import sun.misc.Unsafe;

public class MMU {
    // void *mmap(void *addr, size_t len, int prot, int flags, int fildes, off_t off);
    // int munmap(void *addr, size_t len);
    // int mprotect(void *addr, size_t len, int prot);

    private static boolean initialized = false;
    private static final Unsafe unsafe = UnsafeHolder.getUnsafe();
    private static long ptr;

    public static boolean init(long lo, long hi) {
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }
        try {
            System.loadLibrary("memory");
            ptr = setup(lo, hi);
            return true;
        } catch (UnsatisfiedLinkError e) {
            System.out.println("cannot load libmemory");
            return false;
        } catch (PosixException e) {
            System.err.println("Error: " + Errno.toString(e.getErrno()));
            return false;
        }
    }

    public static long getSegfaultAddress() {
        long val = unsafe.getLong(ptr);
        if (val != 0) {
            unsafe.putLong(ptr, 0);
        }
        return val;
    }

    private static native long setup(long lo, long hi) throws PosixException;

    public static native long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException;

    public static native int munmap(long addr, long len) throws PosixException;

    public static native int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException;
}
