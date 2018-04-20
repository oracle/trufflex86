package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Cxe extends AMD64Instruction {
    protected static final int CBW = 0;
    protected static final int CWDE = 1;
    protected static final int CDQE = 2;

    private final int size;
    private final String name;

    @Child protected ReadNode readSrc;
    @Child protected WriteNode writeDst;

    protected Cxe(long pc, byte[] instruction, int size, String name) {
        super(pc, instruction);
        this.size = size;
        this.name = name;
    }

    protected void createChildrenIfNecessary() {
        if (readSrc == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RegisterAccessFactory regs = state.getRegisters();
            switch (size) {
                case 0:
                    readSrc = regs.getRegister(Register.AL).createRead();
                    writeDst = regs.getRegister(Register.AX).createWrite();
                    break;
                case 1:
                    readSrc = regs.getRegister(Register.AX).createRead();
                    writeDst = regs.getRegister(Register.EAX).createWrite();
                    break;
                case 2:
                    readSrc = regs.getRegister(Register.EAX).createRead();
                    writeDst = regs.getRegister(Register.RAX).createWrite();
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public static class Cbw extends Cxe {
        public Cbw(long pc, byte[] instruction) {
            super(pc, instruction, CBW, "cbw");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            byte value = readSrc.executeI8(frame);
            writeDst.executeI16(frame, value);
            return next();
        }
    }

    public static class Cwde extends Cxe {
        public Cwde(long pc, byte[] instruction) {
            super(pc, instruction, CWDE, "cwde");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            short value = readSrc.executeI16(frame);
            writeDst.executeI32(frame, value);
            return next();
        }
    }

    public static class Cdqe extends Cxe {
        public Cdqe(long pc, byte[] instruction) {
            super(pc, instruction, CDQE, "cdqe");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            createChildrenIfNecessary();
            int value = readSrc.executeI32(frame);
            writeDst.executeI64(frame, value);
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name};
    }
}
