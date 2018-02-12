package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class AMD64BasicBlock extends AMD64Node {
    @Children private AMD64Instruction[] instructions;
    @CompilationFinal(dimensions = 1) private AMD64BasicBlock[] successors;

    public long index;

    public AMD64BasicBlock(AMD64Instruction[] instructions) {
        assert instructions.length > 0;
        this.instructions = instructions;
    }

    public boolean contains(long address) {
        for (AMD64Instruction insn : instructions) {
            if (insn.getPC() == address) {
                return true;
            }
        }
        return false;
    }

    public void setSuccessors(AMD64BasicBlock[] successors) {
        this.successors = successors;
    }

    public AMD64BasicBlock[] getSuccessors() {
        return successors;
    }

    public AMD64BasicBlock getSuccessor(long pc) {
        if (successors == null) {
            return null;
        }
        for (AMD64BasicBlock block : successors) {
            if (block.getAddress() == pc) {
                return block;
            }
        }
        return null;
    }

    public long[] getBTA() {
        return instructions[instructions.length - 1].getBTA();
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public long getAddress() {
        return instructions[0].getPC();
    }

    public int getInstructionCount() {
        return instructions.length;
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
