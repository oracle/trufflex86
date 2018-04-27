package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadFlagsNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Pushf extends AMD64Instruction {
    @Child protected ReadFlagsNode readFlags;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryWriteNode writeMemory;

    protected Pushf(long pc, byte[] instruction) {
        super(pc, instruction);

        setGPRReadOperands(new RegisterOperand(Register.RSP));
        setGPRWriteOperands(new RegisterOperand(Register.RSP));
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            assert readRSP == null;
            assert writeRSP == null;
            assert writeMemory == null;

            ArchitecturalState state = getContextReference().get().getState();
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            readFlags = insert(new ReadFlagsNode());
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            writeMemory = state.createMemoryWrite();
        }
    }

    public static class Pushfw extends Pushf {
        public Pushfw(long pc, byte[] instruction) {
            super(pc, instruction);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = readFlags.executeI16(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 2;
            writeMemory.executeI16(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Pushfq extends Pushf {
        public Pushfq(long pc, byte[] instruction) {
            super(pc, instruction);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long value = readFlags.executeI64(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 8;
            writeMemory.executeI64(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pushf"};
    }
}
