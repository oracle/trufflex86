package org.graalvm.vm.x86.isa.instruction;

import java.util.HashSet;
import java.util.Set;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.ReadFlagNode;
import org.graalvm.vm.x86.node.ReadNode;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.RepeatingNode;

public class Rep extends AMD64Instruction {
    private final AMD64Instruction insn; // not a child, just for lazy initialization of loop node
    @Child private LoopNode loop;
    private final String[] disasm;

    public Rep(long pc, byte[] instruction, AMD64Instruction insn) {
        this(pc, instruction, "rep", insn);
    }

    protected Rep(long pc, byte[] instruction, String name, AMD64Instruction insn) {
        super(pc, instruction);
        this.insn = insn;
        String[] asm = insn.getDisassemblyComponents();
        disasm = new String[asm.length + 1];
        disasm[0] = name;
        System.arraycopy(asm, 0, disasm, 1, asm.length);
    }

    private static interface UsesRegisters {
        public Operand[] getReadOperands();

        public Operand[] getWriteOperands();
    }

    private static class RepBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        public RepBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
            }
            return rcx != 0;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }
    }

    private static class RepzBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private ReadFlagNode readZF;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        public RepzBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
            readZF = state.getRegisters().getZF().createRead();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
            }
            boolean zf = readZF.execute(frame);
            return rcx != 0 && zf;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }
    }

    private static class RepnzBody extends AMD64Node implements RepeatingNode, UsesRegisters {
        @Child private ReadNode readRCX;
        @Child private ReadFlagNode readZF;
        @Child private WriteNode writeRCX;
        @Child private AMD64Instruction insn;

        public RepnzBody(ArchitecturalState state, AMD64Instruction insn) {
            this.insn = insn;
            readRCX = state.getRegisters().getRegister(Register.RCX).createRead();
            writeRCX = state.getRegisters().getRegister(Register.RCX).createWrite();
            readZF = state.getRegisters().getZF().createRead();
        }

        public boolean executeRepeating(VirtualFrame frame) {
            long rcx = readRCX.executeI64(frame);
            if (rcx != 0) {
                insn.executeInstruction(frame);
                rcx--;
                writeRCX.executeI64(frame, rcx);
            }
            boolean zf = readZF.execute(frame);
            return rcx != 0 && !zf;
        }

        @Override
        public Operand[] getReadOperands() {
            Register[] reads = insn.getUsedGPRRead();
            Set<Register> regs = new HashSet<>();
            for (Register r : reads) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }

        @Override
        public Operand[] getWriteOperands() {
            Register[] writes = insn.getUsedGPRWrite();
            Set<Register> regs = new HashSet<>();
            for (Register r : writes) {
                regs.add(r);
            }
            regs.add(Register.RCX);
            Operand[] ops = new Operand[regs.size()];
            int i = 0;
            for (Register r : regs) {
                ops[i++] = new RegisterOperand(r);
            }
            return ops;
        }
    }

    public static class Repz extends Rep {
        public Repz(long pc, byte[] instruction, AMD64Instruction insn) {
            super(pc, instruction, "repz", insn);
        }

        @Override
        protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
            return new RepzBody(state, body);
        }
    }

    public static class Repnz extends Rep {
        public Repnz(long pc, byte[] instruction, AMD64Instruction insn) {
            super(pc, instruction, "repnz", insn);
        }

        @Override
        protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
            return new RepnzBody(state, body);
        }
    }

    protected RepeatingNode createRepeatingNode(ArchitecturalState state, AMD64Instruction body) {
        return new RepBody(state, body);
    }

    @Override
    public Register[] getUsedGPRRead() {
        createChildrenIfNecessary();
        return super.getUsedGPRRead();
    }

    @Override
    public Register[] getUsedGPRWrite() {
        createChildrenIfNecessary();
        return super.getUsedGPRWrite();
    }

    protected void createChildrenIfNecessary() {
        if (loop == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            ArchitecturalState state = getContextReference().get().getState();
            RepeatingNode body = createRepeatingNode(state, insn);
            UsesRegisters r = (UsesRegisters) body;
            setGPRReadOperands(r.getReadOperands());
            setGPRWriteOperands(r.getWriteOperands());
            loop = insert(Truffle.getRuntime().createLoopNode(body));
        }
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        createChildrenIfNecessary();
        loop.executeLoop(frame);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return disasm;
    }
}
