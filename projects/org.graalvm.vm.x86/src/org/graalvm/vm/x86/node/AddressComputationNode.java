package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.MemoryOperand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.SegmentRegister;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AddressComputationNode extends AMD64Node {
    private final long displacement;
    private final long scale;
    private final Register baseReg;
    private final Register indexReg;
    private final SegmentRegister segment;

    @Child private ReadNode base;
    @Child private ReadNode index;
    @Child private ReadNode segmentBase;

    public AddressComputationNode(ArchitecturalState state, MemoryOperand operand, long pc) {
        displacement = operand.getDisplacement();
        scale = operand.getScale();
        baseReg = operand.getBase();
        indexReg = operand.getIndex();
        segment = operand.getSegment();

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
        if (segment != null) {
            segmentBase = state.getRegisters().getFS().createRead();
        }
    }

    public long execute(VirtualFrame frame) {
        long seg = 0;
        if (segment != null) {
            seg = segmentBase.executeI64(frame);
        }
        long baseaddr = 0;
        if (base != null) {
            baseaddr = base.executeI64(frame);
        }
        long indexval = 0;
        if (index != null) {
            indexval = index.executeI64(frame);
        }
        long addr = seg + displacement + baseaddr + (indexval << scale);
        return addr;
    }
}
