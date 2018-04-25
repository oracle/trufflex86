package org.graalvm.vm.x86.node.flow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class TraceTooLargeException extends ControlFlowException {
    private static final long serialVersionUID = 1L;

    public final long pc;

    public TraceTooLargeException(long pc) {
        this.pc = pc;
    }
}
