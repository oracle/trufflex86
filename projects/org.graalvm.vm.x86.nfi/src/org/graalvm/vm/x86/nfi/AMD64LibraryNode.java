package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.MemoryPage;
import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AMD64RootNode;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixPointer;
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
        interpreter = new InterpreterStartNode(descriptor.getFilename());
        root = new InterpreterRootNode(state);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        InteropFunctionPointers ptrs = interpreter.execute(frame);
        ContextReference<AMD64Context> ctxref = getContextReference();
        AMD64Context ctx = ctxref.get();

        // load library
        VirtualMemory mem = ctx.getMemory();
        long len = mem.roundToPageSize(AMD64.SCRATCH_SIZE);
        MemoryPage scratch = mem.allocate(len);
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, scratch.base);
        CString.strcpy(ptr, libname);
        ctx.setScratchMemory(scratch.base);
        long interoplibname = scratch.base;
        long handle = root.executeInterop(frame, ctx.getSigaltstack(), ctx.getReturnAddress(), ptrs.loadLibrary, interoplibname, 0, 0, 0, 0, 0);

        return new AMD64Library(ctxref, ptrs.loadLibrary, ptrs.releaseLibrary, ptrs.getSymbol, handle);
    }
}
