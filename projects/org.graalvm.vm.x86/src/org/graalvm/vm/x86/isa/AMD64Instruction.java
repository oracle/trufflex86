package org.graalvm.vm.x86.isa;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class AMD64Instruction extends AMD64Node {
    public final long pc;
    @CompilationFinal(dimensions = 1) protected final byte[] instruction;

    protected AMD64Instruction(long pc, byte[] instruction) {
        this.pc = pc;
        this.instruction = instruction;
    }

    public abstract long executeInstruction(VirtualFrame frame);

    protected abstract String[] disassemble();

    public boolean isControlFlow() {
        return false;
    }

    public long[] getBTA() {
        return null;
    }

    public int getSize() {
        return instruction.length;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(instruction, instruction.length);
    }

    public long getPC() {
        return pc;
    }

    public long next() {
        return getPC() + getSize();
    }

    public String[] getDisassemblyComponents() {
        return disassemble();
    }

    public String getDisassembly() {
        String[] parts = disassemble();
        if (parts.length == 1) {
            return parts[0];
        } else {
            return parts[0] + "\t" + Stream.of(parts).skip(1).collect(Collectors.joining(","));
        }
    }

    @Override
    public String toString() {
        return getDisassembly();
    }
}
