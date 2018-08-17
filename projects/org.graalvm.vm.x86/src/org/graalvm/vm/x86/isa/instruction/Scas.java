package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Scas extends AMD64Instruction {
    private final String name;

    @Child protected ReadNode readA;
    @Child protected ReadNode readRDI;
    @Child protected MemoryReadNode readMemory;
    @Child protected ReadFlagNode readDF;
    @Child protected WriteNode writeRDI;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected Scas(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RDI));
    }

    protected void createChildNodes(Register a) {
        ArchitecturalState state = getState();
        RegisterAccessFactory regs = state.getRegisters();
        readA = regs.getRegister(a).createRead();
        readRDI = regs.getRegister(Register.RDI).createRead();
        readDF = regs.getDF().createRead();
        readMemory = state.createMemoryRead();
        writeRDI = regs.getRegister(Register.RDI).createWrite();
        writeCF = regs.getCF().createWrite();
        writeOF = regs.getOF().createWrite();
        writeSF = regs.getSF().createWrite();
        writeZF = regs.getZF().createWrite();
        writePF = regs.getPF().createWrite();
        writeAF = regs.getAF().createWrite();
    }

    public static class Scasb extends Scas {
        public Scasb(long pc, byte[] instruction) {
            super(pc, instruction, "scasb");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AL);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rdi = readRDI.executeI64(frame);
            byte a = readA.executeI8(frame);
            byte b = readMemory.executeI8(rdi);

            byte result = (byte) (a - b);

            boolean overflow = (byte) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Byte.toUnsignedInt(a) < Byte.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity(result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rdi--;
            } else {
                rdi++;
            }

            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Scasw extends Scas {
        public Scasw(long pc, byte[] instruction) {
            super(pc, instruction, "scasw");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.AX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rdi = readRDI.executeI64(frame);
            short a = readA.executeI16(frame);
            short b = readMemory.executeI16(rdi);

            short result = (short) (a - b);

            boolean overflow = (short) ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Short.toUnsignedInt(a) < Short.toUnsignedInt(b);
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rdi -= 2;
            } else {
                rdi += 2;
            }

            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Scasd extends Scas {
        public Scasd(long pc, byte[] instruction) {
            super(pc, instruction, "scasd");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.EAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rdi = readRDI.executeI64(frame);
            int a = readA.executeI32(frame);
            int b = readMemory.executeI32(rdi);

            int result = a - b;

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Integer.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rdi -= 4;
            } else {
                rdi += 4;
            }

            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    public static class Scasq extends Scas {
        public Scasq(long pc, byte[] instruction) {
            super(pc, instruction, "scasq");
        }

        @Override
        protected void createChildNodes() {
            createChildNodes(Register.RAX);
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            long rdi = readRDI.executeI64(frame);
            long a = readA.executeI64(frame);
            long b = readMemory.executeI64(rdi);

            long result = a - b;

            boolean overflow = ((a ^ b) & (a ^ result)) < 0;
            boolean carry = Long.compareUnsigned(a, b) < 0;
            boolean adjust = (((a ^ b) ^ result) & 0x10) != 0;

            writeCF.execute(frame, carry);
            writeOF.execute(frame, overflow);
            writeSF.execute(frame, result < 0);
            writeZF.execute(frame, result == 0);
            writePF.execute(frame, Flags.getParity((byte) result));
            writeAF.execute(frame, adjust);

            boolean df = readDF.execute(frame);
            if (df) {
                rdi -= 8;
            } else {
                rdi += 8;
            }

            writeRDI.executeI64(frame, rdi);

            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
