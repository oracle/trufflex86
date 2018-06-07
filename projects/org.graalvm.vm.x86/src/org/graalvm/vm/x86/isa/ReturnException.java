package org.graalvm.vm.x86.isa;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ReturnException extends ControlFlowException {
    private static final long serialVersionUID = 2044196786464045261L;

    private final long bta;

    public ReturnException(long bta) {
        this.bta = bta;
    }

    public long getBTA() {
        return bta;
    }
}
