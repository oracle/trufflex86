package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Movs extends AMD64Instruction {
    private final String name;

    @Child protected ReadNode readRSI;
    @Child protected ReadNode readRDI;
    @Child protected MemoryReadNode readMemory;
    @Child protected MemoryWriteNode writeMemory;
    @Child protected ReadFlagNode readDF;
    @Child protected WriteNode writeRSI;
    @Child protected WriteNode writeRDI;

    protected Movs(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RSI), new RegisterOperand(Register.RDI));
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readRSI = regs.getRegister(Register.RSI).createRead();
        readRDI = regs.getRegister(Register.RDI).createRead();
        readDF = regs.getDF().createRead();
        readMemory = state.createMemoryRead();
        writeMemory = state.createMemoryWrite();
        writeRSI = regs.getRegister(Register.RSI).createWrite();
        writeRDI = regs.getRegister(Register.RDI).createWrite();
    }

    public static class Movsb extends Movs {
        public Movsb(long pc, byte[] instruction) {
            super(pc, instruction, "movsb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            byte val = readMemory.executeI8(rsi);
            writeMemory.executeI8(rdi, val);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi--;
                rdi--;
            } else {
                rsi++;
                rdi++;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Movsw extends Movs {
        public Movsw(long pc, byte[] instruction) {
            super(pc, instruction, "movsw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            short val = readMemory.executeI16(rsi);
            writeMemory.executeI16(rdi, val);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 2;
                rdi -= 2;
            } else {
                rsi += 2;
                rdi += 2;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Movsd extends Movs {
        public Movsd(long pc, byte[] instruction) {
            super(pc, instruction, "movsd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            int val = readMemory.executeI32(rsi);
            writeMemory.executeI32(rdi, val);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 4;
                rdi -= 4;
            } else {
                rsi += 4;
                rdi += 4;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Movsq extends Movs {
        public Movsq(long pc, byte[] instruction) {
            super(pc, instruction, "movsq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            long val = readMemory.executeI64(rsi);
            writeMemory.executeI64(rdi, val);

            boolean df = readDF.execute(frame);
            if (df) {
                rsi -= 8;
                rdi -= 8;
            } else {
                rsi += 8;
                rdi += 8;
            }

            writeRSI.executeI64(frame, rsi);
            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
