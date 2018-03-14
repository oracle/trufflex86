package org.graalvm.vm.x86.isa.instruction;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Prefetch extends AMD64Instruction {
    public static final int PREFETCHT0 = 0;
    public static final int PREFETCHT1 = 1;
    public static final int PREFETCHT2 = 2;
    public static final int PREFETCHNTA = 3;

    private final int type;
    private final Operand operand;

    protected Prefetch(long pc, byte[] instruction, Operand operand, int type) {
        super(pc, instruction);
        this.operand = operand;
        this.type = type;
    }

    public Prefetch(long pc, byte[] instruction, OperandDecoder decoder, int type) {
        this(pc, instruction, decoder.getOperand1(OperandDecoder.R8), type);
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        // TODO: implement?
        return next();
    }

    @Override
    protected String[] disassemble() {
        if (type == PREFETCHNTA) {
            return new String[]{"prefetchnta", operand.toString()};
        } else {
            return new String[]{"prefetcht" + type, operand.toString()};
        }
    }
}
