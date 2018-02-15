package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

public class AVXRegisterOperand extends Operand {
    private final int register;
    private final int size;

    public AVXRegisterOperand(int reg, int size) {
        this.register = reg;
        this.size = size;
    }

    public int getRegister() {
        return register;
    }

    @Override
    public String toString() {
        switch (size) {
            case 64:
                return "mm" + register;
            case 128:
                return "xmm" + register;
            case 256:
                return "ymm" + register;
            case 512:
                return "zmm" + register;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        return state.getRegisters().getAVXRegister(register).createRead();
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        return state.getRegisters().getAVXRegister(register).createWrite();
    }

    @Override
    public int getSize() {
        return size / 8;
    }
}
