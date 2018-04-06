package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagsNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Sahf extends AMD64Instruction {
    @Child private ReadNode readAH;
    @Child private WriteFlagsNode writeFlags;

    public Sahf(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (readAH == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readAH = regs.getRegister(Register.AH).createRead();
            writeFlags = insert(new WriteFlagsNode());
        }
        byte value = readAH.executeI8(frame);
        writeFlags.executeI8(frame, value);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"sahf"};
    }
}
