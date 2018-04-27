package org.graalvm.vm.x86.isa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class AMD64Instruction extends AMD64Node {
    public final long pc;
    @CompilationFinal(dimensions = 1) protected final byte[] instruction;

    @CompilationFinal(dimensions = 1) protected Operand[] gprReadOperands = new Operand[0];
    @CompilationFinal(dimensions = 1) protected Operand[] gprWriteOperands = new Operand[0];

    protected AMD64Instruction(long pc, byte[] instruction) {
        this.pc = pc;
        this.instruction = instruction;
    }

    protected void setGPRReadOperands(Operand... operands) {
        gprReadOperands = operands;
    }

    protected void setGPRWriteOperands(Operand... operands) {
        gprWriteOperands = operands;
    }

    public abstract long executeInstruction(VirtualFrame frame);

    protected abstract String[] disassemble();

    public Register[] getUsedGPRRead() {
        CompilerAsserts.neverPartOfCompilation();
        Set<Register> regs = new HashSet<>();
        // gpr read operands
        for (Operand operand : gprReadOperands) {
            for (Register reg : operand.getRegisters()) {
                regs.add(reg.getRegister());
            }
        }
        // memory access operands
        for (Operand operand : gprWriteOperands) {
            if (operand instanceof MemoryOperand) {
                for (Register reg : operand.getRegisters()) {
                    regs.add(reg.getRegister());
                }
            }
        }
        return regs.toArray(new Register[regs.size()]);
    }

    public Register[] getUsedGPRWrite() {
        // only register operands can write to registers
        CompilerAsserts.neverPartOfCompilation();
        Set<Register> regs = new HashSet<>();
        // gpr read operands
        for (Operand operand : gprWriteOperands) {
            if (operand instanceof RegisterOperand) {
                RegisterOperand op = (RegisterOperand) operand;
                if (op.getRegister() == null) {
                    System.out.println(this);
                    throw new AssertionError();
                }
                regs.add(op.getRegister().getRegister());
            }
        }
        return regs.toArray(new Register[regs.size()]);
    }

    public boolean isControlFlow() {
        return false;
    }

    public long[] getBTA() {
        return null;
    }

    public int getSize() {
        return instruction.length;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(instruction, instruction.length);
    }

    public long getPC() {
        return pc;
    }

    public long next() {
        return getPC() + getSize();
    }

    public String[] getDisassemblyComponents() {
        return disassemble();
    }

    public String getDisassembly() {
        CompilerAsserts.neverPartOfCompilation();
        String[] parts = disassemble();
        if (parts.length == 1) {
            return parts[0];
        } else {
            return parts[0] + "\t" + Stream.of(parts).skip(1).collect(Collectors.joining(","));
        }
    }

    @Override
    public String toString() {
        return getDisassembly();
    }
}
