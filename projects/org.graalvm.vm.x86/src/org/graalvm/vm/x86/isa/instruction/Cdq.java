package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Cdq extends AMD64Instruction {
    @Child private ReadNode readEAX;
    @Child private WriteNode writeEDX;

    public Cdq(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRReadOperands(new RegisterOperand(Register.EAX));
        setGPRWriteOperands(new RegisterOperand(Register.EDX));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (readEAX == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readEAX = regs.getRegister(Register.EAX).createRead();
            writeEDX = regs.getRegister(Register.EDX).createWrite();
        }
        int rax = readEAX.executeI32(frame);
        if (rax < 0) {
            writeEDX.executeI32(frame, -1);
        } else {
            writeEDX.executeI32(frame, 0);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cdq"};
    }
}
