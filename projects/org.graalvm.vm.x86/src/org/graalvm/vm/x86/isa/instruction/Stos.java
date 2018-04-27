package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.MemoryWriteNode;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Stos extends AMD64Instruction {
    private final String name;
    @Child protected ReadFlagNode readDF;
    @Child protected ReadNode readSrc;
    @Child protected ReadNode readDst;
    @Child protected WriteNode writeDst;
    @Child protected MemoryWriteNode writeMemory;

    protected Stos(long pc, byte[] instruction, String name) {
        super(pc, instruction);
        this.name = name;

        setGPRReadOperands(new RegisterOperand(Register.RAX), new RegisterOperand(Register.RDI));
        setGPRWriteOperands(new RegisterOperand(Register.RDI));
    }

    protected void createChildrenIfNecessary(Register src) {
        if (readDF == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            readDF = regs.getDF().createRead();
            readSrc = regs.getRegister(src).createRead();
            readDst = regs.getRegister(Register.RDI).createRead();
            writeDst = regs.getRegister(Register.RDI).createWrite();
            writeMemory = state.createMemoryWrite();
        }
    }

    public static class Stosb extends Stos {
        public Stosb(long pc, byte[] instruction) {
            super(pc, instruction, "stosb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AL);
            boolean df = readDF.execute(frame);
            byte al = readSrc.executeI8(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI8(rdi, al);
            if (df) {
                rdi--;
            } else {
                rdi++;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosw extends Stos {
        public Stosw(long pc, byte[] instruction) {
            super(pc, instruction, "stosw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.AX);
            boolean df = readDF.execute(frame);
            short ax = readSrc.executeI16(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI16(rdi, ax);
            if (df) {
                rdi -= 2;
            } else {
                rdi += 2;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosd extends Stos {
        public Stosd(long pc, byte[] instruction) {
            super(pc, instruction, "stosd");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.EAX);
            boolean df = readDF.execute(frame);
            int eax = readSrc.executeI32(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI32(rdi, eax);
            if (df) {
                rdi -= 4;
            } else {
                rdi += 4;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    public static class Stosq extends Stos {
        public Stosq(long pc, byte[] instruction) {
            super(pc, instruction, "stosq");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary(Register.RAX);
            boolean df = readDF.execute(frame);
            long rax = readSrc.executeI64(frame);
            long rdi = readDst.executeI64(frame);
            writeMemory.executeI64(rdi, rax);
            if (df) {
                rdi -= 8;
            } else {
                rdi += 8;
            }
            writeDst.executeI64(frame, rdi);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
