package org.graalvm.vm.x86.isa;

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
}
