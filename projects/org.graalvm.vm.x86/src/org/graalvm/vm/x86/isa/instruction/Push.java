package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Push extends AMD64Instruction {
    private final Operand operand;

    @Child protected ReadNode readSrc;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryWriteNode writeMemory;

    protected Push(long pc, byte[] instruction, Operand src) {
        super(pc, instruction);
        this.operand = src;
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreter();
            assert readRSP == null;
            assert writeRSP == null;
            assert writeMemory == null;

            ArchitecturalState state = getContextReference().get().getState();
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            readSrc = operand.createRead(state, next());
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            writeMemory = state.createMemoryWrite();
        }
    }

    public static class Pushw extends Push {
        public Pushw(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = readSrc.executeI16(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 2;
            writeMemory.executeI16(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Pushq extends Push {
        public Pushq(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long value = readSrc.executeI64(frame);
            long rsp = readRSP.executeI64(frame);
            rsp -= 8;
            writeMemory.executeI64(rsp, value);
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"push", operand.toString()};
    }
}
