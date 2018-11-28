package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.InteropFunctionPointers;
import org.graalvm.vm.x86.node.AMD64RootNode;
import org.graalvm.vm.x86.posix.InteropErrorException;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixException;
import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.nfi.types.NativeLibraryDescriptor;

public class AMD64LibraryNode extends AMD64RootNode {
    @Child private InterpreterStartNode interpreter;

    @Child private InterpreterRootNode root;

    private final String libname;

    public AMD64LibraryNode(TruffleLanguage<AMD64Context> language, FrameDescriptor fd, NativeLibraryDescriptor descriptor) {
        super(language, fd);
        this.libname = descriptor.getFilename();
        ArchitecturalState state = language.getContextReference().get().getState();
        interpreter = new InterpreterStartNode();
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static PosixPointer strcpy(PosixPointer dst, String src) {
        return CString.strcpy(dst, src);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        ContextReference<AMD64Context> ctxref = getContextReference();
        AMD64Context ctx = ctxref.get();
        VirtualMemory mem = ctx.getMemory();

        InteropFunctionPointers ptrs = ctx.getInteropFunctionPointers();
        if (ptrs == null) {
            ptrs = interpreter.execute(frame);
            ctx.setInteropFunctionPointers(ptrs);

            long len = mem.roundToPageSize(AMD64.SCRATCH_SIZE);
            MemoryPage scratch = mem.allocate(len);
            try {
                mem.mprotect(scratch.base, scratch.size, true, true, true);
            } catch (PosixException e) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException(e);
            }
            ctx.setScratchMemory(scratch.base);
        }

        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, ctx.getScratchMemory());

        // load library
        long interoplibname = ctx.getScratchMemory();
        if (libname != null) {
            strcpy(ptr, libname);
        } else {
            interoplibname = 0;
        }

        long handle;
        try {
            handle = root.executeInterop(frame, ctx.getSigaltstack(), ctx.getReturnAddress(), ptrs.loadLibrary, interoplibname, 0, 0, 0, 0, 0);
        } catch (InteropErrorException e) {
            throw new UnsatisfiedLinkError(e.getMessage());
        }

        return new AMD64Library(ctxref, ptrs.loadLibrary, ptrs.releaseLibrary, ptrs.getSymbol, handle);
    }
}
