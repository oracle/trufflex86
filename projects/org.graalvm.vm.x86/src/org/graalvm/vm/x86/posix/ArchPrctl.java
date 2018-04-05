package org.graalvm.vm.x86.posix;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.everyware.posix.api.Errno;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class ArchPrctl extends AMD64Node {
    public static final int ARCH_SET_GS = 0x1001;
    public static final int ARCH_SET_FS = 0x1002;
    public static final int ARCH_GET_FS = 0x1003;
    public static final int ARCH_GET_GS = 0x1004;

    @Child private RegisterReadNode readFS;
    @Child private RegisterReadNode readGS;
    @Child private RegisterWriteNode writeFS;
    @Child private RegisterWriteNode writeGS;
    @Child private MemoryWriteNode writeMemory;

    public long execute(VirtualFrame frame, int code, long value) throws SyscallException {
        switch (code) {
            case ARCH_SET_GS:
                if (writeGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeGS = state.getRegisters().getGS().createWrite();
                }
                writeGS.executeI64(frame, value);
                return 0;
            case ARCH_SET_FS:
                if (writeGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeFS = state.getRegisters().getFS().createWrite();
                }
                writeFS.executeI64(frame, value);
                return 0;
            case ARCH_GET_FS:
                if (readFS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    readFS = state.getRegisters().getFS().createRead();
                }
                if (writeMemory == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeMemory = state.createMemoryWrite();
                }
                writeMemory.executeI64(value, readFS.executeI64(frame));
                return 0;
            case ARCH_GET_GS:
                if (readGS == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    readGS = state.getRegisters().getGS().createRead();
                }
                if (writeMemory == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    ArchitecturalState state = getContextReference().get().getState();
                    writeMemory = state.createMemoryWrite();
                }
                writeMemory.executeI64(value, readGS.executeI64(frame));
                return 0;
            default:
                CompilerDirectives.transferToInterpreter();
                System.out.printf("arch_prctl(0x%x): invalid code\n", code);
        }
        throw new SyscallException(Errno.EINVAL);
    }
}
