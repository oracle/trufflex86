package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Register;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AddressComputationNode extends AMD64Node {
    private final long displacement;
    private final long scale;
    private final Register baseReg;
    private final Register indexReg;

    @Child private ReadNode base;
    @Child private ReadNode index;

    public AddressComputationNode(ArchitecturalState state, MemoryOperand operand, long pc) {
        displacement = operand.getDisplacement();
        scale = operand.getScale();
        baseReg = operand.getBase();
        indexReg = operand.getIndex();

        assert scale >= 0 && scale <= 3;

        if (baseReg != null) {
            if (baseReg == Register.RIP) {
                base = new ImmediateNode(pc);
            } else {
                base = state.getRegisters().getRegister(baseReg).createRead();
            }
        }
        if (indexReg != null) {
            index = state.getRegisters().getRegister(indexReg).createRead();
        }
    }

    public long execute(VirtualFrame frame) {
        long baseaddr = 0;
        if (base != null) {
            baseaddr = base.executeI64(frame);
        }
        long indexval = 0;
        if (index != null) {
            indexval = index.executeI64(frame);
        }
        long addr = displacement + baseaddr + (indexval << scale);
        return addr;
    }
}
