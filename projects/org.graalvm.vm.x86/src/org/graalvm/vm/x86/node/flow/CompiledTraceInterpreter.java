package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class CompiledTraceInterpreter extends AMD64Node {
    private final RootCallTarget callTarget;
    private final InterTraceCallTarget interpreter;

    @Child private ReadNode readPC;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    public CompiledTraceInterpreter(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        interpreter = new InterTraceCallTarget(language, fd);
        callTarget = Truffle.getRuntime().createCallTarget(interpreter);

        ArchitecturalState state = language.getContextReference().get().getState();
        readPC = state.getRegisters().getPC().createRead();
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        CpuState state = readState.execute(frame, pc);
        try {
            state = (CpuState) callTarget.call(state);
        } catch (RetException e) {
            state = e.getState();
        }
        writeState.execute(frame, state);
        return state.rip;
    }

    public long execute(VirtualFrame frame, long pc) {
        CpuState state = readState.execute(frame, pc);
        try {
            state = (CpuState) callTarget.call(state);
        } catch (RetException e) {
            state = e.getState();
        }
        writeState.execute(frame, state);
        return state.rip;
    }
}
