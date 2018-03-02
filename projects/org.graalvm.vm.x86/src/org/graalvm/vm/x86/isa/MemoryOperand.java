package org.graalvm.vm.x86.isa;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.AddressComputationNode;
import org.graalvm.vm.x86.node.AddressedMemoryReadNode;
import org.graalvm.vm.x86.node.AddressedMemoryWriteNode;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerAsserts;

public class MemoryOperand extends Operand {
    private final SegmentRegister segment;
    private final Register base;
    private final Register index;
    private final int scale;
    private final long displacement;

    public MemoryOperand(SegmentRegister segment, Register base) {
        this.segment = segment;
        this.base = base.get64bit();
        this.index = null;
        this.scale = 0;
        this.displacement = 0;
    }

    public MemoryOperand(SegmentRegister segment, Register base, long displacement) {
        this.segment = segment;
        this.base = base.get64bit();
        this.index = null;
        this.scale = 0;
        this.displacement = displacement;
    }

    public MemoryOperand(SegmentRegister segment, Register base, Register index, int scale) {
        this.segment = segment;
        this.base = base != null ? base.get64bit() : null;
        this.index = index != null ? index.get64bit() : null;
        this.scale = scale;
        this.displacement = 0;
    }

    public MemoryOperand(SegmentRegister segment, Register base, Register index, int scale, long displacement) {
        this.segment = segment;
        this.base = base != null ? base.get64bit() : base;
        this.index = index != null ? index.get64bit() : index;
        this.scale = scale;
        this.displacement = displacement;
    }

    public MemoryOperand(SegmentRegister segment, long displacement) {
        this.segment = segment;
        this.base = null;
        this.index = null;
        this.scale = 0;
        this.displacement = displacement;
    }

    public MemoryOperand getInSegment(SegmentRegister seg) {
        MemoryOperand op = new MemoryOperand(seg, base, index, scale, displacement);
        assert op.base == base;
        assert op.index == index;
        return op;
    }

    public SegmentRegister getSegment() {
        return segment;
    }

    public Register getBase() {
        return base;
    }

    public Register getIndex() {
        return index;
    }

    public int getScale() {
        return scale;
    }

    public long getDisplacement() {
        return displacement;
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        StringBuilder buf = new StringBuilder();
        if (base != null) {
            buf.append(base.toString());
        }
        if (index != null) {
            if (buf.length() > 0) {
                buf.append("+");
            }
            buf.append(index.toString());
            if (scale > 0) {
                buf.append("*" + (1 << scale));
            }
        }
        if (buf.length() == 0 || displacement != 0) {
            boolean add = displacement >= 0;
            if (add && buf.length() > 0) {
                buf.append("+");
            }
            if (add) {
                buf.append(String.format("0x%x", displacement));
            } else {
                buf.append(String.format("-0x%x", -displacement));
            }
        }
        if (segment != null) {
            return segment + ":[" + buf + "]";
        } else {
            return "[" + buf + "]";
        }
    }

    @Override
    public ReadNode createRead(ArchitecturalState state, long pc) {
        AddressComputationNode address = new AddressComputationNode(state, this, pc);
        MemoryReadNode memory = state.createMemoryRead();
        return new AddressedMemoryReadNode(address, memory);
    }

    @Override
    public WriteNode createWrite(ArchitecturalState state, long pc) {
        AddressComputationNode address = new AddressComputationNode(state, this, pc);
        MemoryWriteNode memory = state.createMemoryWrite();
        return new AddressedMemoryWriteNode(address, memory);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
