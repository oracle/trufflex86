package org.graalvm.vm.x86.isa;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class IllegalInstruction extends AMD64Instruction {
    private final String errorMessage;

    public IllegalInstruction(long pc, byte[] info) {
        super(pc, info);
        String msg = String.format("%016X: Unknown opcode", pc);
        if (info.length > 0) {
            String insn = IntStream.range(0, instruction.length).mapToObj(i -> info[i]).map(x -> String.format("%02x", x & 0xff)).collect(Collectors.joining(" "));
            errorMessage = msg + " (instruction: " + insn + ")";
        } else {
            errorMessage = msg;
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreter();
        throw new RuntimeException(errorMessage);
    }

    @Override
    protected String[] disassemble() {
        if (instruction.length > 0) {
            String data = IntStream.range(0, instruction.length).mapToObj(i -> instruction[i]).map(x -> String.format("0x%02x", x)).collect(Collectors.joining(", "));
            return new String[]{"db", data};
        } else {
            return new String[]{"???"};
        }
    }
}
