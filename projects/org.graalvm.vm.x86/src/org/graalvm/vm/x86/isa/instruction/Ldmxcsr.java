package org.graalvm.vm.x86.isa.instruction;

import java.util.logging.Logger;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.Operand;
import org.graalvm.vm.x86.isa.OperandDecoder;
import org.graalvm.vm.x86.node.ReadNode;

import com.everyware.util.log.Levels;
import com.everyware.util.log.Trace;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

public class Ldmxcsr extends AMD64Instruction {
    private static final Logger log = Trace.create(Ldmxcsr.class);

    private final Operand operand;

    @Child private ReadNode readSrc;

    protected Ldmxcsr(long pc, byte[] instruction, Operand operand) {
        super(pc, instruction);
        this.operand = operand;

        setGPRWriteOperands(operand);
    }

    public Ldmxcsr(long pc, byte[] instruction, OperandDecoder operands) {
        this(pc, instruction, operands.getOperand1(OperandDecoder.R32));
    }

    @Override
    public long executeInstruction(VirtualFrame frame) {
        if (readSrc == null) { // TODO: use createChildren/createChildNodes
            CompilerDirectives.transferToInterpreterAndInvalidate();
            log.log(Levels.WARNING, String.format("Stub instruction LDMXCSR executed at 0x%016x", getPC()));
            ArchitecturalState state = getContextReference().get().getState();
            readSrc = operand.createRead(state, next());
        }
        // TODO: write MXCSR
        return next();
    }

    @Override
    protected String[] disassemble() {
        return new String[]{"ldmxcsr", operand.toString()};
    }
}
