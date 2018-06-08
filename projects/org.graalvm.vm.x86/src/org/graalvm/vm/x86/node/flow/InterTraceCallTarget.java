package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64RootNode;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterTraceCallTarget extends AMD64RootNode {
    @Child private InterTraceDispatchNode dispatch;

    protected InterTraceCallTarget(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
        ArchitecturalState state = language.getContextReference().get().getState();
        dispatch = new InterTraceDispatchNode(state);
    }

    @Override
    public CpuState execute(VirtualFrame frame) {
        CpuState state = (CpuState) frame.getArguments()[0];
        return dispatch.execute(frame, state);
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String toString() {
        long addr = dispatch.getStartTrace().trace.getStartAddress();
        if (addr == -1) {
            return "InterTraceCallTarget[???]";
        } else {
            return String.format("InterTraceCallTarget[0x%016x]", addr);
        }
    }
}
