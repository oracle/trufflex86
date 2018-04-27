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

public class Cwd extends AMD64Instruction {
    @Child private ReadNode readAX;
    @Child private WriteNode writeDX;

    public Cwd(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRReadOperands(new RegisterOperand(Register.AX));
        setGPRWriteOperands(new RegisterOperand(Register.DX));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (readAX == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readAX = regs.getRegister(Register.AX).createRead();
            writeDX = regs.getRegister(Register.DX).createWrite();
        }
        short ax = readAX.executeI16(frame);
        if (ax < 0) {
            writeDX.executeI16(frame, (short) -1);
        } else {
            writeDX.executeI16(frame, (short) 0);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cwd"};
    }
}
