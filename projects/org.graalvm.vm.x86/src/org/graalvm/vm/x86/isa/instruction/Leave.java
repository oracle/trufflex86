package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Leave extends AMD64Instruction {
    @Child protected RegisterReadNode readRBP;
    @Child protected RegisterWriteNode writeRBP;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryReadNode readMemory;

    protected Leave(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            assert readRSP == null;
            assert writeRSP == null;
            assert readMemory == null;

            ArchitecturalState state = getContextReference().get().getState();
            AMD64Register rbp = state.getRegisters().getRegister(Register.RBP);
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            readRBP = rbp.createRead();
            writeRBP = rbp.createWrite();
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            readMemory = state.createMemoryRead();
        }
    }

    public static class Leaveq extends Leave {
        public Leaveq(long pc, byte[] instruction) {
            super(pc, instruction);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsp = readRBP.executeI64(frame);
            long value = readMemory.executeI64(rsp);
            writeRBP.executeI64(frame, value);
            rsp += 8;
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"leave"};
    }
}
