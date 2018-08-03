package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Pop extends AMD64Instruction {
    private final Operand operand;

    @Child protected WriteNode writeDst;
    @Child protected RegisterReadNode readRSP;
    @Child protected RegisterWriteNode writeRSP;
    @Child protected MemoryReadNode readMemory;

    protected Pop(long pc, byte[] instruction, Operand src) {
        super(pc, instruction);
        this.operand = src;

        setGPRReadOperands(new RegisterOperand(Register.RSP));
        setGPRWriteOperands(operand, new RegisterOperand(Register.RSP));
    }

    protected void createChildrenIfNecessary() {
        if (readRSP == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            assert readRSP == null;
            assert writeRSP == null;
            assert readMemory == null;

            ArchitecturalState state = getContextReference().get().getState();
            AMD64Register rsp = state.getRegisters().getRegister(Register.RSP);
            writeDst = operand.createWrite(state, next());
            readRSP = rsp.createRead();
            writeRSP = rsp.createWrite();
            readMemory = state.createMemoryRead();
        }
    }

    public static class Popw extends Pop {
        public Popw(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsp = readRSP.executeI64(frame);
            short value = readMemory.executeI16(rsp);
            rsp += 2;
            writeRSP.executeI64(frame, rsp);
            writeDst.executeI16(frame, value);
            return next();
        }
    }

    public static class Popq extends Pop {
        public Popq(long pc, byte[] instruction, Operand src) {
            super(pc, instruction, src);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsp = readRSP.executeI64(frame);
            long value = readMemory.executeI64(rsp);
            rsp += 8;
            writeRSP.executeI64(frame, rsp);
            writeDst.executeI64(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"pop", operand.toString()};
    }
}
