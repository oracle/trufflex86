package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Setcc extends AMD64Instruction {
    private final String name;
    private final Operand operand;

    @Child protected WriteNode writeDst;

    protected Setcc(long pc, byte[] instruction, String name, Operand operand) {
        super(pc, instruction);
        this.name = name;
        this.operand = operand;

        setGPRWriteOperands(operand);
    }

    @Override
    protected void createChildNodes() {
        ArchitecturalState state = getState();
        writeDst = operand.createWrite(state, next());
    }

    public static class Seta extends Setcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Seta(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "seta", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readCF = regs.getCF().createRead();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean value = !cf && !zf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setae extends Setcc {
        @Child private ReadFlagNode readCF;

        public Setae(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setae", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readCF = regs.getCF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean value = !cf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setb extends Setcc {
        @Child private ReadFlagNode readCF;

        public Setb(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setb", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readCF = regs.getCF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean value = cf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setbe extends Setcc {
        @Child private ReadFlagNode readCF;
        @Child private ReadFlagNode readZF;

        public Setbe(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setbe", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readCF = regs.getCF().createRead();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean cf = readCF.execute(frame);
            boolean zf = readZF.execute(frame);
            boolean value = cf || zf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Sete extends Setcc {
        @Child private ReadFlagNode readZF;

        public Sete(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "sete", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            boolean value = zf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setg extends Setcc {
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Setg(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setg", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readZF = regs.getZF().createRead();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            boolean value = !zf && (sf == of);
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setge extends Setcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Setge(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setge", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            boolean value = sf == of;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setl extends Setcc {
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Setl(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setl", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            boolean value = sf != of;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setle extends Setcc {
        @Child private ReadFlagNode readZF;
        @Child private ReadFlagNode readSF;
        @Child private ReadFlagNode readOF;

        public Setle(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setle", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readZF = regs.getZF().createRead();
            readSF = regs.getSF().createRead();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            boolean sf = readSF.execute(frame);
            boolean of = readOF.execute(frame);
            boolean value = zf || (sf != of);
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setne extends Setcc {
        @Child private ReadFlagNode readZF;

        public Setne(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setne", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readZF = regs.getZF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean zf = readZF.execute(frame);
            boolean value = !zf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setno extends Setcc {
        @Child private ReadFlagNode readOF;

        public Setno(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setno", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean of = readOF.execute(frame);
            boolean value = !of;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setnp extends Setcc {
        @Child private ReadFlagNode readPF;

        public Setnp(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setnp", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readPF = regs.getPF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean pf = readPF.execute(frame);
            boolean value = !pf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setns extends Setcc {
        @Child private ReadFlagNode readSF;

        public Setns(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setns", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readSF = regs.getSF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean value = !sf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Seto extends Setcc {
        @Child private ReadFlagNode readOF;

        public Seto(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "seto", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readOF = regs.getOF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean of = readOF.execute(frame);
            boolean value = of;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Setp extends Setcc {
        @Child private ReadFlagNode readPF;

        public Setp(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "setp", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readPF = regs.getPF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean pf = readPF.execute(frame);
            boolean value = pf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    public static class Sets extends Setcc {
        @Child private ReadFlagNode readSF;

        public Sets(long pc, byte[] instruction, OperandDecoder operands) {
            super(pc, instruction, "sets", operands.getOperand1(OperandDecoder.R8));
        }

        @Override
        protected void createChildNodes() {
            super.createChildNodes();
            ArchitecturalState state = getState();
            RegisterAccessFactory regs = state.getRegisters();
            readSF = regs.getSF().createRead();
        }

        @Override
        public long executeInstruction(VirtualFrame frame) {
            boolean sf = readSF.execute(frame);
            boolean value = sf;
            writeDst.executeI8(frame, (byte) (value ? 1 : 0));
            return next();
        }
    }

    @Override
    protected String[] disassemble() {
        return new String[]{name, operand.toString()};
    }
}
