package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.memory.PosixVirtualMemoryPointer;
import org.graalvm.vm.memory.VirtualMemory;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AMD64Node;

import com.everyware.posix.api.PosixPointer;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.nfi.types.NativeSignature;
import com.oracle.truffle.nfi.types.NativeTypeMirror;

public class AMD64FunctionCallNode extends AMD64Node {
    @Child private InterpreterRootNode root;
    @Child private AMD64ArgumentConversionNode converter = new AMD64ArgumentConversionNode();

    public AMD64FunctionCallNode(ArchitecturalState state) {
        root = new InterpreterRootNode(state);
    }

    @TruffleBoundary
    private static NativeTypeMirror getType(NativeSignature signature, int i) {
        return signature.getArgTypes().get(i);
    }

    public long execute(VirtualFrame frame, AMD64Function func, Object[] args) {
        AMD64Context ctx = getContextReference().get();
        VirtualMemory mem = ctx.getMemory();
        long pname = ctx.getScratchMemory();
        PosixPointer ptr = new PosixVirtualMemoryPointer(mem, pname);
        long sp = ctx.getSigaltstack();
        long ret = ctx.getReturnAddress();
        long[] rawargs = new long[6];
        NativeSignature signature = func.getSignature();
        for (int i = 0; i < args.length; i++) {
            ConversionResult result = converter.execute(getType(signature, i), ptr, args[i]);
            rawargs[i] = result.value;
            ptr = result.ptr;
        }
        long a1 = rawargs[0];
        long a2 = rawargs[1];
        long a3 = rawargs[2];
        long a4 = rawargs[3];
        long a5 = rawargs[4];
        long a6 = rawargs[5];
        return root.executeInterop(frame, sp, ret, func.getFunction(), a1, a2, a3, a4, a5, a6);
    }
}
