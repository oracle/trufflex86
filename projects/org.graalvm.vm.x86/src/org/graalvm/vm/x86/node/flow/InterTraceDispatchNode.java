package org.graalvm.vm.x86.node.flow;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.CpuRuntimeException;
import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.isa.IllegalInstructionException;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;
import org.graalvm.vm.x86.node.init.InitializeFromCpuStateNode;
import org.graalvm.vm.x86.posix.ProcessExitException;

import com.everyware.posix.api.Signal;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;

public class InterTraceDispatchNode extends AbstractDispatchNode {
    @Child private ReadNode readPC;
    @Child private WriteNode writePC;

    @Child private CopyToCpuStateNode readState = new CopyToCpuStateNode();
    @Child private InitializeFromCpuStateNode writeState = new InitializeFromCpuStateNode();

    @Child private IndirectCallNode node = Truffle.getRuntime().createIndirectCallNode();

    private Map<Long, CompiledTrace> traces;

    @CompilationFinal private AMD64Language language;
    @CompilationFinal private FrameDescriptor frameDescriptor;

    @CompilationFinal public static boolean PRINT_STATS = false;

    private int noSuccessor = 0;
    private int hasSuccessor = 0;

    public InterTraceDispatchNode(ArchitecturalState state) {
        readPC = state.getRegisters().getPC().createRead();
        writePC = state.getRegisters().getPC().createWrite();
        traces = new HashMap<>();
    }

    @TruffleBoundary
    private CompiledTrace get(long pc) {
        if (language == null) {
            AMD64Context ctx = getContextReference().get();
            language = getRootNode().getLanguage(AMD64Language.class);
            frameDescriptor = ctx.getFrameDescriptor();
        }
        CompiledTrace trace = traces.get(pc);
        if (trace == null) {
            TraceCallTarget target = new TraceCallTarget(language, frameDescriptor);
            trace = new CompiledTrace(target);
            traces.put(pc, trace);
        }
        return trace;
    }

    @TruffleBoundary
    private void printStats() {
        System.out.printf("Traces: %d\n", traces.size());
        System.out.printf("Successor chain used: %d\n", hasSuccessor);
        System.out.printf("No successor chain used: %d\n", noSuccessor);
    }

    @Override
    public long execute(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        long pc = readPC.executeI64(frame);
        CpuState state = readState.execute(frame, pc);
        try {
            CompiledTrace trace = get(state.rip);
            while (true) {
                pc = state.rip;
                state = (CpuState) trace.callTarget.call(state);
                CompiledTrace next = trace.getNext(state.rip);
                if (next == null) {
                    noSuccessor++;
                    next = get(state.rip);
                    trace.setNext(next);
                } else {
                    hasSuccessor++;
                }
                trace = next;
            }
        } catch (ProcessExitException e) {
            if (PRINT_STATS) {
                printStats();
            }
            return e.getCode();
        } catch (CpuRuntimeException e) {
            if (e.getCause() instanceof IllegalInstructionException) {
                return 128 + Signal.SIGILL;
            } else if (e.getCause() instanceof SegmentationViolation) {
                return 128 + Signal.SIGSEGV;
            } else {
                return 127;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return 127;
        }
    }
}
