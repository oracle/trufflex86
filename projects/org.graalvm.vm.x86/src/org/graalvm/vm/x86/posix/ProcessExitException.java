package org.graalvm.vm.x86.posix;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ProcessExitException extends ControlFlowException {
    private static final long serialVersionUID = -7483493093129513658L;

    private int code;

    public ProcessExitException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
