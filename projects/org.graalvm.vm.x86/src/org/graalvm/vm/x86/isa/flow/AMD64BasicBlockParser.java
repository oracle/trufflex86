package org.graalvm.vm.x86.isa.flow;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.AMD64InstructionDecoder;
import org.graalvm.vm.x86.isa.CodeReader;

public class AMD64BasicBlockParser {
    public static AMD64BasicBlock parse(CodeReader reader) {
        List<AMD64Instruction> instructions = new ArrayList<>();
        while (reader.isAvailable()) {
            AMD64Instruction insn = AMD64InstructionDecoder.decode(reader.getPC(), reader);
            instructions.add(insn);
            if (insn.isControlFlow()) {
                break;
            }
        }
        return new AMD64BasicBlock(instructions.toArray(new AMD64Instruction[instructions.size()]));
    }
}
