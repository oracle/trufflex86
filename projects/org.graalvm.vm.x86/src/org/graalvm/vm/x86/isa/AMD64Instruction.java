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
        return pc + instruction.length;
    }

    @Override
    public String toString() {
        String[] parts = disassemble();
        if (parts.length == 1) {
            return parts[0];
        } else {
            return String.format("%s\t%s", parts[0], Stream.of(parts).skip(1).collect(Collectors.joining(",")));
        }
    }
}
