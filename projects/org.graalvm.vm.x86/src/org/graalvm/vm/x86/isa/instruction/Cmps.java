package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Flags;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.MemoryReadNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cmps extends AMD64Instruction {
    private final String name;

    @Child protected ReadNode readRSI;
    @Child protected ReadNode readRDI;
    @Child protected MemoryReadNode readMemory;
    @Child protected ReadFlagNode readDF;
    @Child protected WriteNode writeRSI;
    @Child protected WriteNode writeRDI;
    @Child protected WriteFlagNode writeCF;
    @Child protected WriteFlagNode writeOF;
    @Child protected WriteFlagNode writeSF;
    @Child protected WriteFlagNode writeZF;
    @Child protected WriteFlagNode writePF;
    @Child protected WriteFlagNode writeAF;

    protected Cmps(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;
    }

    protected void createChildrenIfNecessary() {
        if (readRSI == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readRSI = regs.getRegister(Register.RSI).createRead();
            readRDI = regs.getRegister(Register.RDI).createRead();
            readDF = regs.getDF().createRead();
            readMemory = state.createMemoryRead();
            writeRSI = regs.getRegister(Register.RSI).createWrite();
            writeRDI = regs.getRegister(Register.RDI).createWrite();
            writeCF = regs.getCF().createWrite();
            writeOF = regs.getOF().createWrite();
            writeSF = regs.getSF().createWrite();
            writeZF = regs.getZF().createWrite();
            writePF = regs.getPF().createWrite();
            writeAF = regs.getAF().createWrite();
        }
    }

    public static class Cmpsb extends Cmps {
        public Cmpsb(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            byte a = readMemory.executeI8(rsi);
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

    public static class Cmpsw extends Cmps {
        public Cmpsw(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            short a = readMemory.executeI16(rsi);
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

    public static class Cmpsd extends Cmps {
        public Cmpsd(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            int a = readMemory.executeI32(rsi);
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

    public static class Cmpsq extends Cmps {
        public Cmpsq(long pc, byte[] instruction) {
            super(pc, instruction, "cmpsq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            long rsi = readRSI.executeI64(frame);
            long rdi = readRDI.executeI64(frame);
            long a = readMemory.executeI64(rsi);
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
