package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.isa.CpuState;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class RetException extends ControlFlowException {
    private static final long serialVersionUID = 5088699537597058521L;

    private final CpuState state;

    public RetException(CpuState state) {
        this.state = state;
    }

    public CpuState getState() {
        return state;
    }
}
