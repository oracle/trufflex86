package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.node.WriteFlagNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Cld extends AMD64Instruction {
    @Child private WriteFlagNode writeDF;

    public Cld(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (writeDF == null) {
            CompilerDirectives.transferToInterpreter();
            ArchitecturalState state = getContextReference().get().getState();
            writeDF = state.getRegisters().getDF().createWrite();
        }
        writeDF.execute(frame, false);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cld"};
    }
}
