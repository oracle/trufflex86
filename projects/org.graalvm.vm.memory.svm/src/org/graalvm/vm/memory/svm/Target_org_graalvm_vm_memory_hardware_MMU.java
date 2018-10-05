package org.graalvm.vm.memory.svm;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

import com.everyware.posix.api.PosixException;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.posix.headers.Errno;
import com.oracle.svm.core.posix.headers.Mman;

@Platforms(Platform.LINUX.class)
@TargetClass(org.graalvm.vm.memory.hardware.MMU.class)
final class Target_org_graalvm_vm_memory_hardware_MMU {
    @Substitute
    private static void loadLibrary() throws UnsatisfiedLinkError {
        // nothing
    }

    @Substitute
    private static long setup(long lo, long hi) throws PosixException {
        return LibMemory.setupSegvHandler(lo, hi);
    }

    @Substitute
    public static long mmap(long addr, long len, boolean r, boolean w, boolean x, boolean fixed, boolean anonymous, boolean shared, int fildes, long off) throws PosixException {
        int prot = PosixUtils.getProtection(r, w, x);
        int flags = 0;
        if (fixed) {
            flags |= Mman.MAP_FIXED();
        }
        if (anonymous) {
            flags |= Mman.MAP_ANONYMOUS();
        }
        if (shared) {
            flags |= Mman.MAP_SHARED();
        } else {
            flags |= Mman.MAP_PRIVATE();
        }
        PointerBase result = Mman.mmap(WordFactory.pointer(addr), WordFactory.unsigned(len), prot, flags, fildes, off);
        if (result.equal(Mman.MAP_FAILED())) {
            int errno = Errno.errno();
            throw new PosixException(errno);
        } else {
            return result.rawValue();
        }
    }

    @Substitute
    public static int munmap(long addr, long len) throws PosixException {
        int result = Mman.munmap(WordFactory.pointer(addr), WordFactory.unsigned(len));
        if (result < 0) {
            int errno = Errno.errno();
            throw new PosixException(errno);
        }
        return result;
    }

    @Substitute
    public static int mprotect(long addr, long len, boolean r, boolean w, boolean x) throws PosixException {
        int prot = PosixUtils.getProtection(r, w, x);
        int result = Mman.mprotect(WordFactory.pointer(addr), WordFactory.unsigned(len), prot);
        if (result < 0) {
            int errno = Errno.errno();
            throw new PosixException(errno);
        }
        return result;
    }
}
