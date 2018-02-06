package org.graalvm.vm.x86.isa;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class AMD64Instruction extends AMD64Node {
    public final long pc;
    @CompilationFinal(dimensions = 1) protected final byte[] instruction;

    protected AMD64Instruction(long pc, byte[] instruction) {
        this.pc = pc;
        this.instruction = instruction;
    }

    protected abstract long executeInstruction(VirtualFrame frame);

    protected abstract String[] disassemble();

    public int getSize() {
        return instruction.length;
    }

    public long getPC() {
        return pc;
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
