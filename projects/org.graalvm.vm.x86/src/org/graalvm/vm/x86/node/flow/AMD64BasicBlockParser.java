package org.graalvm.vm.x86.node.flow;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.SegmentationViolationInstruction;

public class AMD64BasicBlockParser {
    public static AMD64BasicBlock parse(CodeReader reader) {
        List<AMD64Instruction> instructions = new ArrayList<>();
        while (reader.isAvailable()) {
            try {
                AMD64Instruction insn = AMD64InstructionDecoder.decode(reader.getPC(), reader);
                instructions.add(insn);
                if (insn.isControlFlow()) {
                    break;
                }
            } catch (SegmentationViolation e) {
                instructions.add(new SegmentationViolationInstruction(e));
                break;
            }
        }
        return new AMD64BasicBlock(instructions.toArray(new AMD64Instruction[instructions.size()]));
    }
}
