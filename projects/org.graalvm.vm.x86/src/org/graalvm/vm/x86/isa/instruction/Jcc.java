package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.RegisterReadNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Jcc extends AMD64Instruction {
    private final String name;
    protected final long bta;

    protected Jcc(long pc, byte[] instruction, int offset, String name) {
        super(pc, instruction);
        this.bta = getPC() + getSize() + offset;
        this.name = name;
    }

    public static class Ja extends Jcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Ja(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "ja");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readCF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readCF = regs.getCF().createRead();
                readZF = regs.getZF().createRead();
            }
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            return (!cf && !zf) ? bta : next();
        }
    }

    public static class Jae extends Jcc {
        @Child private ReadFlagNode readCF;

        public Jae(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jae");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readCF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readCF = regs.getCF().createRead();
            }
            boolean cf = readCF.execute(frame);
            return !cf ? bta : next();
        }
    }

    public static class Jb extends Jcc {
        @Child private ReadFlagNode readCF;

        public Jb(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jb");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readCF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readCF = regs.getCF().createRead();
            }
            boolean cf = readCF.execute(frame);
            return cf ? bta : next();
        }
    }

    public static class Jbe extends Jcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Jbe(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jbe");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readCF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readCF = regs.getCF().createRead();
                readZF = regs.getZF().createRead();
            }
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            return (cf || zf) ? bta : next();
        }
    }

    public static class Jrcxz extends Jcc {
        @Child private RegisterReadNode readRCX;

        public Jrcxz(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jrczx");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readRCX == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readRCX = regs.getRegister(Register.RAX).createRead();
            }
            long value = readRCX.executeI64(frame);
            return (value == 0) ? bta : next();
        }
    }

    public static class Je extends Jcc {
        @Child private ReadFlagNode readZF;

        public Je(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "je");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readZF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readZF = regs.getZF().createRead();
            }
            boolean zf = readZF.execute(frame);
            return zf ? bta : next();
        }
    }

    public static class Jg extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readOF;

        public Jg(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jg");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
                readZF = regs.getZF().createRead();
                readOF = regs.getOF().createRead();
            }
            boolean sf = readSF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean of = readOF.execute(frame);
            return (!zf && (sf == of)) ? bta : next();
        }
    }

    public static class Jge extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Jge(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jge");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
                readOF = regs.getOF().createRead();
            }
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            return (sf == of) ? bta : next();
        }
    }

    public static class Jl extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Jl(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jl");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
                readOF = regs.getOF().createRead();
            }
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            return (sf != of) ? bta : next();
        }
    }

    public static class Jle extends Jcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readOF;

        public Jle(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jle");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
                readZF = regs.getZF().createRead();
                readOF = regs.getOF().createRead();
            }
            boolean sf = readSF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean of = readOF.execute(frame);
            return (zf || (sf != of)) ? bta : next();
        }
    }

    public static class Jne extends Jcc {
        @Child private ReadFlagNode readZF;

        public Jne(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jne");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readZF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readZF = regs.getZF().createRead();
            }
            boolean zf = readZF.execute(frame);
            return !zf ? bta : next();
        }
    }

    public static class Jno extends Jcc {
        @Child private ReadFlagNode readOF;

        public Jno(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jno");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readOF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readOF = regs.getOF().createRead();
            }
            boolean of = readOF.execute(frame);
            return !of ? bta : next();
        }
    }

    public static class Jnp extends Jcc {
        @Child private ReadFlagNode readPF;

        public Jnp(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jnp");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readPF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readPF = regs.getPF().createRead();
            }
            boolean pf = readPF.execute(frame);
            return !pf ? bta : next();
        }
    }

    public static class Jns extends Jcc {
        @Child private ReadFlagNode readSF;

        public Jns(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jns");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
            }
            boolean sf = readSF.execute(frame);
            return !sf ? bta : next();
        }
    }

    public static class Jo extends Jcc {
        @Child private ReadFlagNode readOF;

        public Jo(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jo");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readOF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readOF = regs.getOF().createRead();
            }
            boolean of = readOF.execute(frame);
            return of ? bta : next();
        }
    }

    public static class Jp extends Jcc {
        @Child private ReadFlagNode readPF;

        public Jp(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "jp");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readPF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readPF = regs.getPF().createRead();
            }
            boolean pf = readPF.execute(frame);
            return pf ? bta : next();
        }
    }

    public static class Js extends Jcc {
        @Child private ReadFlagNode readSF;

        public Js(long pc, byte[] instruction, int offset) {
            super(pc, instruction, offset, "js");
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            if (readSF == null) {
                CompilerDirectives.transferToInterpreter();
                RegisterAccessFactory regs = getContextReference().get().getState().getRegisters();
                readSF = regs.getSF().createRead();
            }
            boolean sf = readSF.execute(frame);
            return sf ? bta : next();
        }
    }

    @Override
    public boolean isControlFlow() {
        return true;
    }

    @Override
    public long[] getBTA() {
        return new long[]{bta, next()};
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, String.format("0x%x", bta)};
    }
}
