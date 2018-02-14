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
    private final Register base;
    private final Register index;
    private final int scale;
    private final long displacement;

    public MemoryOperand(Register base) {
        this.base = base;
        this.index = null;
        this.scale = 0;
        this.displacement = 0;
    }

    public MemoryOperand(Register base, long displacement) {
        this.base = base;
        this.index = null;
        this.scale = 0;
        this.displacement = displacement;
    }

    public MemoryOperand(Register base, Register index, int scale) {
        assert base != index;
        this.base = base;
        this.index = index;
        this.scale = scale;
        this.displacement = 0;
    }

    public MemoryOperand(Register base, Register index, int scale, long displacement) {
        assert base != index;
        this.base = base;
        this.index = index;
        this.scale = scale;
        this.displacement = displacement;
    }

    public MemoryOperand(long displacement) {
        this.base = null;
        this.index = null;
        this.scale = 0;
        this.displacement = displacement;
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
            if (buf.length() > 0) {
                buf.append("+");
            }
            buf.append(String.format("0x%x", displacement));
        }
        return "[" + buf + "]";
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
