package org.graalvm.vm.x86.node.flow;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.x86.AMD64Language;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node.Child;

public class TraceRegistry {
    private final AMD64Language language;
    private final FrameDescriptor frameDescriptor;

    @Child private IndirectCallNode node = Truffle.getRuntime().createIndirectCallNode();

    private Map<Long, CompiledTrace> traces;

    public TraceRegistry(AMD64Language language, FrameDescriptor frameDescriptor) {
        this.language = language;
        this.frameDescriptor = frameDescriptor;
        traces = new HashMap<>();
    }

    @TruffleBoundary
    public CompiledTrace get(long pc) {
        CompiledTrace trace = traces.get(pc);
        if (trace == null) {
            TraceCallTarget target = new TraceCallTarget(language, frameDescriptor);
            trace = new CompiledTrace(target);
            traces.put(pc, trace);
        }
        return trace;
    }

    public int size() {
        return traces.size();
    }
}
