package org.graalvm.vm.x86.isa.instruction;

import java.util.logging.Logger;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.WriteNode;

import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Stmxcsr extends AMD64Instruction {
    private static final Logger log = Trace.create(Stmxcsr.class);

    private final Operand operand;

    @Child private WriteNode writeDst;

    protected Stmxcsr(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;

        setGPRWriteOperands(operand);
    }

    public Stmxcsr(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R32));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (writeDst == null) { // TODO: use createChildren/createChildNodes
            CompilerDirectives.transferToInterpreterAndInvalidate();
            log.log(Levels.WARNING, String.format("Stub instruction STMXCSR executed at 0x%016x", getPC()));
            ArchitecturalState state = getContextReference().get().getState();
            writeDst = operand.createWrite(state, next());
        }
        // TODO: write MXCSR
        writeDst.executeI32(frame, 0x1F80);
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"stmxcsr", operand.toString()};
    }
}
