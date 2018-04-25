package org.graalvm.vm.x86.node.flow;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;

public class CompiledTrace {
    public final RootCallTarget callTarget;
    public final TraceCallTarget trace;

    private int usedSuccessors;
    private CompiledTrace[] successors;

    public CompiledTrace(TraceCallTarget trace) {
        this.trace = trace;
        callTarget = Truffle.getRuntime().createCallTarget(trace);
        usedSuccessors = 0;
        successors = new CompiledTrace[8];
    }

    public CompiledTrace getNext(long pc) {
        for (int i = 0; i < usedSuccessors; i++) {
            if (successors[i].trace.getStartAddress() == pc) {
                // TODO: sort as LRU
                return successors[i];
            }
        }
        return null;
    }

    public void setNext(CompiledTrace trace) {
        // trace already registered?
        for (int i = 0; i < usedSuccessors; i++) {
            if (successors[i] == trace) {
                return;
            }
        }

        if (usedSuccessors < successors.length) {
            successors[usedSuccessors++] = trace;
        }
    }
}
