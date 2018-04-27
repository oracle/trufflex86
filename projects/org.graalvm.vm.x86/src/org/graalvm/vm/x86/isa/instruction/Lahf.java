package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Lahf extends AMD64Instruction {
    @Child private ReadFlagsNode readFlags;
    @Child private WriteNode writeAH;

    public Lahf(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRWriteOperands(new RegisterOperand(Register.RAX));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (writeAH == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            writeAH = regs.getRegister(Register.AH).createWrite();
            readFlags = insert(new ReadFlagsNode());
        }
        byte value = readFlags.executeI8(frame);
        writeAH.executeI8(frame, value);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"lahf"};
    }
}
