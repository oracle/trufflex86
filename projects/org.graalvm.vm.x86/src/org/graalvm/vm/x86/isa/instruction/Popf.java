package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteFlagsNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Popf extends AMD64Instruction {
    @Child protected WriteFlagsNode writeFlags;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryReadNode readMemory;

    protected Popf(long pc, byte[] instruction) {
        super(pc, instruction);
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreter();
            assert readRSP == null;
            assert writeRSP == null;
            assert readMemory == null;

            ArchitecturalState state = getContextReference().get().getState();
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            writeFlags = insert(new WriteFlagsNode());
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            readMemory = state.createMemoryRead();
        }
    }

    public static class Popfw extends Popf {
        public Popfw(long pc, byte[] instruction) {
            super(pc, instruction);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsp = readRSP.executeI64(frame);
            short value = readMemory.executeI16(rsp);
            writeFlags.executeI16(frame, value);
            rsp += 2;
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    public static class Popfq extends Popf {
        public Popfq(long pc, byte[] instruction) {
            super(pc, instruction);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsp = readRSP.executeI64(frame);
            long value = readMemory.executeI64(rsp);
            writeFlags.executeI64(frame, value);
            rsp += 8;
            writeRSP.executeI64(frame, rsp);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"popf"};
    }
}
