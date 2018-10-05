package org.graalvm.vm.memory.svm;

import com.oracle.svm.core.posix.headers.Mman;

public class PosixUtils {
    public static int getProtection(boolean r, boolean w, boolean x) {
        int prot = 0;
        if (r) {
            prot |= Mman.PROT_READ();
        }
        if (w) {
            prot |= Mman.PROT_WRITE();
        }
        if (x) {
            prot |= Mman.PROT_EXEC();
        }
        return prot;
    }
}
