package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public abstract class Operand {
    public abstract ReadNode createRead(ArchitecturalState state, long pc);

    public abstract WriteNode createWrite(ArchitecturalState state, long pc);

    public abstract int getSize();

    public Register[] getRegisters() {
        return new Register[0];
    }
}
