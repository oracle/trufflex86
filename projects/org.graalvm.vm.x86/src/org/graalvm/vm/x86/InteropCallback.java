package org.graalvm.vm.x86;

import org.graalvm.vm.x86.posix.SyscallException;

public interface InteropCallback {
    long call(int id, long a1, long a2, long a3, long a4, long a5, long a6) throws SyscallException;

    long call(int id, long a1, long a2, long a3, long a4, long a5, long a6, long f1, long f2, long f3, long f4, long f5, long f6, long f7, long f8) throws SyscallException;
}
