package org.graalvm.vm.x86.isa.instruction;

import java.util.logging.Logger;

import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.WriteNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Fnstcw extends AMD64Instruction {
    private static final Logger log = Trace.create(Stmxcsr.class);

    private final Operand operand;

    @Child private WriteNode writeDst;

    protected Fnstcw(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;

        setGPRWriteOperands(operand);
    }

    public Fnstcw(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R16));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (writeDst == null) { // TODO: use createChildren/createChildNodes
            CompilerDirectives.transferToInterpreterAndInvalidate();
            log.log(Levels.WARNING, String.format("Stub instruction FNSTCW executed at 0x%016x", getPC()));
            ArchitecturalState state = getContextReference().get().getState();
            writeDst = operand.createWrite(state, next());
        }
        // TODO: write FPU CW
        writeDst.executeI16(frame, (short) 0x037F);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"fnstcw", operand.toString()};
    }
}
