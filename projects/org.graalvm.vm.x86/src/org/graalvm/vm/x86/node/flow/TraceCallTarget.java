package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64RootNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class TraceCallTarget extends AMD64RootNode {
    @Child private InitializeFromCpuStateNode write = new InitializeFromCpuStateNode();
    @Child private CopyToCpuStateNode read = new CopyToCpuStateNode();
    @Child private TraceDispatchNode dispatch;

    protected TraceCallTarget(TruffleLanguage<?> language, FrameDescriptor fd) {
        super(language, fd);
    }

    @Override
    public CpuState execute(VirtualFrame frame) {
        if (dispatch == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            dispatch = insert(new TraceDispatchNode(state));
        }
        CpuState initialState = (CpuState) frame.getArguments()[0];
        write.execute(frame, initialState);
        long pc = dispatch.execute(frame);
        return read.execute(frame, pc);
    }

    public long getStartAddress() {
        return dispatch.getStartAddress();
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String toString() {
        long addr = dispatch.getStartAddress();
        if (addr == -1) {
            return "TraceCallTarget[???]";
        } else {
            return String.format("TraceCallTarget[0x%016x]", addr);
        }
    }
}
