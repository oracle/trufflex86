package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AMD64Node;

import com.everyware.posix.api.CString;
import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AMD64SymbolLookupNode extends AMD64Node {
    @Child private InterpreterRootNode root;

    public AMD64SymbolLookupNode(ArchitecturalState state) {
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static PosixPointer strcpy(PosixPointer dst, String src) {
        return CString.strcpy(dst, src);
    }

    public long executeLookup(VirtualFrame frame, AMD64Library lib, String name) {
        AMD64Context ctx = getContextReference().get();
        long handle = lib.getHandle();
        VirtualMemory mem = ctx.getMemory();
        long pname = ctx.getScratchMemory();
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, pname);
        strcpy(ptr, name);
        long sp = ctx.getSigaltstack();
        long ret = ctx.getReturnAddress();
        return root.executeInterop(frame, sp, ret, lib.getSymbol(), handle, pname, 0, 0, 0, 0);
    }
}
