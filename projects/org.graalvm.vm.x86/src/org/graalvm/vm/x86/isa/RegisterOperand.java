package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class RegisterOperand extends Operand {
    private final Register register;

    public RegisterOperand(Register reg) {
        this.register = reg;
    }

    public Register getRegister() {
        return register;
    }

    @Override
    public String toString() {
        return register.toString();
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        return state.getRegisters().getRegister(register).createRead();
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        return state.getRegisters().getRegister(register).createWrite();
    }

    @Override
    public int getSize() {
        return register.getSize();
    }

    @Override
    public Register[] getRegisters() {
        return new Register[]{register};
    }
}
