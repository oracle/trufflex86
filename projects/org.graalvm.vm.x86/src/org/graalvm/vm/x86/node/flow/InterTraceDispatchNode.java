package org.graalvm.vm.x86.node.flow;

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.Options;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;

import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.RepeatingNode;

public class InterTraceDispatchNode extends AbstractDispatchNode {
    @Child private ReadNode readPC;
    @Child private WriteNode writePC;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    private final TraceRegistry traces;

    @CompilationFinal public static boolean PRINT_STATS = getBoolean(Options.PRINT_DISPATCH_STATS);
    @CompilationFinal public static boolean USE_LOOP_NODE = getBoolean(Options.USE_LOOP_NODE);

    private int noSuccessor = 0;
    private int hasSuccessor = 0;

    private long insncnt = 0;

    @Child private LoopNode loop = Truffle.getRuntime().createLoopNode(new LoopBody());

    private CpuState state;
    private CompiledTrace currentTrace;

    @CompilationFinal private CompiledTrace startTrace;

    public InterTraceDispatchNode(ArchitecturalState state) {
        readPC = state.getRegisters().getPC().createRead();
        writePC = state.getRegisters().getPC().createWrite();
        traces = state.getTraceRegistry();
    }

    @TruffleBoundary
    private void printStats() {
        Trace.log.printf("Traces: %d\n", traces.size());
        Trace.log.printf("Successor chain used: %d\n", hasSuccessor);
        Trace.log.printf("No successor chain used: %d\n", noSuccessor);
        Trace.log.printf("Executed instructions: %d\n", insncnt);
    }

    public CompiledTrace getStartTrace() {
        return startTrace;
    }

    private class LoopBody extends AMD64Node implements RepeatingNode {
        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            try {
                state = (CpuState) currentTrace.callTarget.call(state);
            } catch (RetException e) {
                state = e.getState();
                throw e;
            }
            CompiledTrace next = currentTrace.getNext(state.rip);
            if (next == null) {
                noSuccessor++;
                next = traces.get(state.rip);
                currentTrace.setNext(next);
            } else {
                hasSuccessor++;
            }
            currentTrace = next;
            insncnt = state.instructionCount;
            return true;
        }
    }

    public CpuState execute(VirtualFrame frame, CpuState cpuState) {
        this.state = cpuState;
        currentTrace = startTrace;
        if (currentTrace == null || currentTrace.trace.getStartAddress() != state.rip) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            currentTrace = traces.get(state.rip);
            startTrace = currentTrace;
        }

        try {
            loop.executeLoop(frame);
        } catch (RetException e) {
            return e.getState();
        }
        throw new AssertionError("loop node must not return");
    }

    @Override
    public long execute(VirtualFrame frame) {
        long pc = readPC.executeI64(frame);
        state = readState.execute(frame, pc);
        currentTrace = startTrace;
        if (currentTrace == null || currentTrace.trace.getStartAddress() != state.rip) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            currentTrace = traces.get(state.rip);
            startTrace = currentTrace;
        }

        if (USE_LOOP_NODE) {
            loop.executeLoop(frame);
            throw new AssertionError("loop node must not return");
        } else {
            while (true) {
                pc = state.rip;
                state = (CpuState) currentTrace.callTarget.call(state);
                CompiledTrace next = currentTrace.getNext(state.rip);
                if (next == null) {
                    noSuccessor++;
                    next = traces.get(state.rip);
                    currentTrace.setNext(next);
                } else {
                    hasSuccessor++;
                }
                currentTrace = next;
                insncnt = state.instructionCount;
            }
        }
    }
}
