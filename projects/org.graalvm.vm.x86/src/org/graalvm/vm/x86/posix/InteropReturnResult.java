package org.graalvm.vm.x86.posix;

import org.graalvm.vm.x86.isa.CpuState;

@SuppressWarnings("serial")
public class InteropReturnResult extends InteropException {
    private final CpuState value;

    public InteropReturnResult(CpuState value) {
        this.value = value;
    }

    public CpuState getState() {
        return value;
    }
}
