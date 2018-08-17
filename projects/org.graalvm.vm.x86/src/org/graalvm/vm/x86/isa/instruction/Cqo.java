package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Cqo extends AMD64Instruction {
    @Child private ReadNode readRAX;
    @Child private WriteNode writeRDX;

    public Cqo(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRReadOperands(new RegisterOperand(Register.RAX));
        setGPRWriteOperands(new RegisterOperand(Register.RDX));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readRAX = regs.getRegister(Register.RAX).createRead();
        writeRDX = regs.getRegister(Register.RDX).createWrite();
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        long rax = readRAX.executeI64(frame);
        if (rax < 0) {
            writeRDX.executeI64(frame, -1L);
        } else {
            writeRDX.executeI64(frame, 0);
        }
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"cqo"};
    }
}
