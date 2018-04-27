package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.RegisterReadNode;
import org.graalvm.vm.x86.node.RegisterWriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Lods extends AMD64Instruction {
    private final String name;

    @Child protected RegisterReadNode readRSI;
    @Child protected RegisterWriteNode writeRSI;
    @Child protected RegisterWriteNode writeA;
    @Child protected MemoryReadNode readMemory;

    protected Lods(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RSI));
        setGPRWriteOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RAX));
    }

    protected void createChildrenIfNecessary(Register a) {
        if (readRSI == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            assert readRSI == null;
            assert writeRSI == null;
            assert writeA == null;
            assert readMemory == null;
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readRSI = regs.getRegister(Register.RSI).createRead();
            writeRSI = regs.getRegister(Register.RSI).createWrite();
            writeA = regs.getRegister(a).createWrite();
            readMemory = state.createMemoryRead();
        }
    }

    public static class Lodsb extends Lods {
        public Lodsb(long pc, byte[] instruction) {
            super(pc, instruction, "lodsb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AL);
            long rsi = readRSI.executeI64(frame);
            byte al = readMemory.executeI8(rsi);
            writeA.executeI8(frame, al);
            writeRSI.executeI64(frame, rsi + 1);
            return next();
        }
    }

    public static class Lodsw extends Lods {
        public Lodsw(long pc, byte[] instruction) {
            super(pc, instruction, "lodsw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AX);
            long rsi = readRSI.executeI64(frame);
            short ax = readMemory.executeI16(rsi);
            writeA.executeI16(frame, ax);
            writeRSI.executeI64(frame, rsi + 2);
            return next();
        }
    }

    public static class Lodsd extends Lods {
        public Lodsd(long pc, byte[] instruction) {
            super(pc, instruction, "lodsd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.EAX);
            long rsi = readRSI.executeI64(frame);
            int eax = readMemory.executeI32(rsi);
            writeA.executeI32(frame, eax);
            writeRSI.executeI64(frame, rsi + 4);
            return next();
        }
    }

    public static class Lodsq extends Lods {
        public Lodsq(long pc, byte[] instruction) {
            super(pc, instruction, "lodsq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.RAX);
            long rsi = readRSI.executeI64(frame);
            long rax = readMemory.executeI64(rsi);
            writeA.executeI64(frame, rax);
            writeRSI.executeI64(frame, rsi + 8);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
