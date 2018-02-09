package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class AMD64BasicBlock extends AMD64Node {
    @CompilationFinal(dimensions = 1) private final AMD64Instruction[] instructions;

    public AMD64BasicBlock(AMD64Instruction[] instructions) {
        assert instructions.length > 0;
        this.instructions = instructions;
    }

    @ExplodeLoop
    public long execute(VirtualFrame frame) {
        long pc = 0;
        for (AMD64Instruction insn : instructions) {
            pc = insn.executeInstruction(frame);
        }
        return pc;
    }

    public AMD64Instruction getLastInstruction() {
        return instructions[instructions.length - 1];
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(String.format("%016x:\n", instructions[0].getPC()));
        for (AMD64Instruction insn : instructions) {
            buf.append(insn).append('\n');
        }
        return buf.toString();
    }
}
